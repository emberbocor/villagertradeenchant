package io.github.emberbocor.villagertradeenchant.client;

import java.util.Comparator;
import java.util.List;

import io.github.emberbocor.villagertradeenchant.VillagerTradeEnchant;
import io.github.emberbocor.villagertradeenchant.enchant.EnchantEntry;
import io.github.emberbocor.villagertradeenchant.menu.EnchantMenu;
import io.github.emberbocor.villagertradeenchant.network.SelectEnchantPayload;
import io.github.emberbocor.villagertradeenchant.platform.ClientServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class EnchantScreen extends AbstractContainerScreen<EnchantMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/villager.png");
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/scroller_disabled");
    private static final ResourceLocation ROW_SPRITE = ResourceLocation.withDefaultNamespace("widget/button");
    private static final ResourceLocation ROW_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/button_highlighted");
    private static final List<ResourceLocation> INPUT_SLOT_ICONS = List.of(
            ResourceLocation.withDefaultNamespace("item/empty_slot_sword"),
            ResourceLocation.withDefaultNamespace("item/empty_slot_pickaxe"),
            ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate"),
            ResourceLocation.fromNamespaceAndPath(VillagerTradeEnchant.MODID, "item/empty_slot_book"));
    private static final Component ENCHANTMENTS_LABEL = Component.translatable("container." + VillagerTradeEnchant.MODID + ".enchantments");
    private static final Component PUT_ITEM_HINT = Component.translatable("container." + VillagerTradeEnchant.MODID + ".put_item");
    private static final Component NO_ENCHANTMENTS_HINT = Component.translatable("container." + VillagerTradeEnchant.MODID + ".no_enchantments");
    private static final int LABEL_COLOR = 4210752;
    private static final int VISIBLE_ROWS = 7;
    private static final int LIST_X = 5;
    private static final int LIST_Y = 18;
    private static final int ROW_WIDTH = 88;
    private static final int ROW_HEIGHT = 20;
    private static final int NAME_X = 4;
    private static final int PRICE_X = 68;
    private static final int NAME_WIDTH = PRICE_X - NAME_X - 2;
    private static final int SCROLL_BAR_X = 94;
    private static final int SCROLL_BAR_HEIGHT = 139;
    private static final int SCROLLER_WIDTH = 6;
    private static final int SCROLLER_HEIGHT = 27;

    private final CyclingSlotBackground inputSlotIcon = new CyclingSlotBackground(EnchantMenu.INPUT_SLOT);
    private List<EnchantEntry> shownEntries = List.of();
    private List<EnchantRow> rows = List.of();
    private int scrollOff;
    private boolean dragging;

    public EnchantScreen(EnchantMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 276;
        inventoryLabelX = 107;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        inputSlotIcon.tick(INPUT_SLOT_ICONS);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        updateRows();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        renderRowTooltip(guiGraphics, mouseX, mouseY);
    }

    private void updateRows() {
        List<EnchantEntry> entries = menu.entries();
        if (!entries.equals(shownEntries)) {
            shownEntries = entries;
            rows = entries.stream()
                    .map(EnchantRow::of)
                    .sorted(Comparator.comparing((EnchantRow row) -> row.name().getString(), String.CASE_INSENSITIVE_ORDER)
                            .thenComparingInt(row -> row.entry().level()))
                    .toList();
            scrollOff = 0;
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, 49 + imageWidth / 2 - font.width(title) / 2, 6, LABEL_COLOR, false);
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL_COLOR, false);
        guiGraphics.drawString(font, ENCHANTMENTS_LABEL, LIST_X - font.width(ENCHANTMENTS_LABEL) / 2 + 48, 6, LABEL_COLOR, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0.0F, 0.0F, imageWidth, imageHeight, 512, 256);
        inputSlotIcon.render(menu, guiGraphics, partialTick, leftPos, topPos);
        renderScroller(guiGraphics);
        if (rows.isEmpty()) {
            renderHint(guiGraphics);
            return;
        }
        EnchantEntry selected = menu.selectedEntry();
        int hoveredRow = rowAt(mouseX, mouseY);
        for (int row = 0; row < Math.min(VISIBLE_ROWS, rows.size()); row++) {
            int index = scrollOff + row;
            EnchantRow enchantRow = rows.get(index);
            int x = leftPos + LIST_X;
            int y = topPos + LIST_Y + row * ROW_HEIGHT;
            boolean highlighted = index == hoveredRow || selected != null && enchantRow.entry().isSameOption(selected);
            guiGraphics.blitSprite(highlighted ? ROW_HIGHLIGHTED_SPRITE : ROW_SPRITE, x, y, ROW_WIDTH, ROW_HEIGHT);
            guiGraphics.drawString(font, enchantRow.fit(font, NAME_WIDTH), x + NAME_X, y + 6, 0xFFFFFFFF, true);
            guiGraphics.renderFakeItem(enchantRow.price(), x + PRICE_X, y + 2);
            guiGraphics.renderItemDecorations(font, enchantRow.price(), x + PRICE_X, y + 2);
        }
    }

    private void renderScroller(GuiGraphics guiGraphics) {
        int x = leftPos + SCROLL_BAR_X;
        int y = topPos + LIST_Y;
        int hidden = rows.size() - VISIBLE_ROWS;
        if (hidden > 0) {
            int offset = scrollOff * (SCROLL_BAR_HEIGHT - SCROLLER_HEIGHT + 1) / hidden;
            guiGraphics.blitSprite(SCROLLER_SPRITE, x, y + offset, SCROLLER_WIDTH, SCROLLER_HEIGHT);
        } else {
            guiGraphics.blitSprite(SCROLLER_DISABLED_SPRITE, x, y, SCROLLER_WIDTH, SCROLLER_HEIGHT);
        }
    }

    private void renderHint(GuiGraphics guiGraphics) {
        Component hint = menu.getSlot(EnchantMenu.INPUT_SLOT).hasItem() ? NO_ENCHANTMENTS_HINT : PUT_ITEM_HINT;
        List<FormattedCharSequence> lines = font.split(hint, ROW_WIDTH - 8);
        int y = topPos + LIST_Y + (SCROLL_BAR_HEIGHT - lines.size() * font.lineHeight) / 2;
        for (FormattedCharSequence line : lines) {
            guiGraphics.drawString(font, line, leftPos + LIST_X + (ROW_WIDTH - font.width(line)) / 2, y, LABEL_COLOR, false);
            y += font.lineHeight;
        }
    }

    private void renderRowTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int index = rowAt(mouseX, mouseY);
        if (index < 0 || !menu.getCarried().isEmpty()) {
            return;
        }
        EnchantRow row = rows.get(index);
        if (mouseX >= leftPos + LIST_X + PRICE_X - 2) {
            guiGraphics.renderTooltip(font, row.price(), mouseX, mouseY);
        } else if (!row.fits(font, NAME_WIDTH)) {
            guiGraphics.renderTooltip(font, row.fullName(), mouseX, mouseY);
        }
    }

    private int rowAt(double mouseX, double mouseY) {
        int x = leftPos + LIST_X;
        int y = topPos + LIST_Y;
        if (mouseX < x || mouseX >= x + ROW_WIDTH || mouseY < y || mouseY >= y + VISIBLE_ROWS * ROW_HEIGHT) {
            return -1;
        }
        int index = scrollOff + (int) (mouseY - y) / ROW_HEIGHT;
        return index < rows.size() ? index : -1;
    }

    private boolean canScroll() {
        return rows.size() > VISIBLE_ROWS;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        dragging = false;
        if (button == 0) {
            int x = leftPos + SCROLL_BAR_X;
            int y = topPos + LIST_Y;
            if (canScroll() && mouseX > x && mouseX < x + SCROLLER_WIDTH && mouseY > y && mouseY <= y + SCROLL_BAR_HEIGHT + 1) {
                dragging = true;
            }
            int index = rowAt(mouseX, mouseY);
            if (index >= 0) {
                select(rows.get(index).entry());
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void select(EnchantEntry entry) {
        entry.enchantment().unwrapKey().ifPresent(key -> {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            menu.select(key, entry.level());
            ClientServices.PLATFORM.sendSelection(new SelectEnchantPayload(menu.containerId, key, entry.level()));
        });
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!dragging) {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        int top = topPos + LIST_Y;
        int hidden = rows.size() - VISIBLE_ROWS;
        float position = ((float) mouseY - top - SCROLLER_HEIGHT / 2.0F) / (SCROLL_BAR_HEIGHT - SCROLLER_HEIGHT);
        scrollOff = Mth.clamp((int) (position * hidden + 0.5F), 0, Math.max(0, hidden));
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (canScroll()) {
            scrollOff = Mth.clamp((int) (scrollOff - scrollY), 0, rows.size() - VISIBLE_ROWS);
        }
        return true;
    }
}
