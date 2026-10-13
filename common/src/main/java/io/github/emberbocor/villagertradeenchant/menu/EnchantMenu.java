package io.github.emberbocor.villagertradeenchant.menu;

import java.util.List;
import java.util.OptionalInt;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.villagertradeenchant.config.Price;
import io.github.emberbocor.villagertradeenchant.enchant.EnchantEntries;
import io.github.emberbocor.villagertradeenchant.enchant.EnchantEntry;
import io.github.emberbocor.villagertradeenchant.network.EnchantEntriesPayload;
import io.github.emberbocor.villagertradeenchant.platform.Services;
import io.github.emberbocor.villagertradeenchant.villager.EnchanterProfession;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class EnchantMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT = 0;
    public static final int PAYMENT_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    private static final int INVENTORY_START = 3;
    private static final int HOTBAR_START = 30;
    private static final int HOTBAR_END = 39;

    @Nullable
    private final Villager villager;
    @Nullable
    private final ServerPlayer owner;
    private final SimpleContainer inputs = new SimpleContainer(2);
    private final ResultContainer result = new ResultContainer();
    private List<EnchantEntry> entries = List.of();
    @Nullable
    private EnchantEntry selected;
    private List<EnchantEntry> sentEntries = List.of();
    private int sentSelection = -1;

    public EnchantMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, null);
    }

    private EnchantMenu(int containerId, Inventory inventory, @Nullable Villager villager) {
        super(Services.PLATFORM.enchantMenuType(), containerId);
        this.villager = villager;
        this.owner = villager != null && inventory.player instanceof ServerPlayer player ? player : null;
        inputs.addListener(this::slotsChanged);

        addSlot(new Slot(inputs, INPUT_SLOT, 136, 37) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        addSlot(new Slot(inputs, PAYMENT_SLOT, 162, 37));
        addSlot(new ResultSlot(220, 37));
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 108 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 108 + column * 18, 142));
        }
    }

    public static void open(Player player, Villager villager) {
        villager.setTradingPlayer(player);
        OptionalInt containerId = player.openMenu(new SimpleMenuProvider(
                (id, inventory, opener) -> new EnchantMenu(id, inventory, villager), villager.getDisplayName()));
        if (containerId.isEmpty()) {
            villager.setTradingPlayer(null);
        }
    }

    public List<EnchantEntry> entries() {
        return entries;
    }

    @Nullable
    public EnchantEntry selectedEntry() {
        return currentEntry();
    }

    public void setEntries(List<EnchantEntry> entries, int selection) {
        this.entries = entries;
        this.selected = selection >= 0 && selection < entries.size() ? entries.get(selection) : null;
    }

    public void select(ResourceLocation enchantment, int level) {
        EnchantEntry entry = entries.stream()
                .filter(candidate -> enchantment.equals(BuiltInRegistries.ENCHANTMENT.getKey(candidate.enchantment())) && candidate.level() == level)
                .findFirst()
                .orElse(null);
        if (entry == null) {
            syncEntries(true);
            return;
        }
        selected = entry;
        movePaymentFromInventory(entry.price());
        slotsChanged(inputs);
    }

    private void movePaymentFromInventory(Price price) {
        ItemStack payment = inputs.getItem(PAYMENT_SLOT);
        if (!payment.isEmpty()) {
            if (!moveItemStackTo(payment, INVENTORY_START, HOTBAR_END, true)) {
                return;
            }
            inputs.setItem(PAYMENT_SLOT, payment);
        }
        if (!inputs.getItem(PAYMENT_SLOT).isEmpty()) {
            return;
        }
        for (int i = INVENTORY_START; i < HOTBAR_END; i++) {
            ItemStack stack = slots.get(i).getItem();
            ItemStack current = inputs.getItem(PAYMENT_SLOT);
            if (stack.is(price.item()) && (current.isEmpty() || ItemStack.isSameItemSameTags(stack, current))) {
                int max = stack.getMaxStackSize();
                int amount = Math.min(max - current.getCount(), stack.getCount());
                ItemStack moved = stack.copyWithCount(current.getCount() + amount);
                stack.shrink(amount);
                inputs.setItem(PAYMENT_SLOT, moved);
                if (moved.getCount() >= max) {
                    break;
                }
            }
        }
    }

    @Override
    public void slotsChanged(Container container) {
        if (villager != null) {
            entries = EnchantEntries.forItem(inputs.getItem(INPUT_SLOT));
            updateResult();
            syncEntries(false);
        }
        super.slotsChanged(container);
    }

    private void syncEntries(boolean force) {
        if (owner == null) {
            return;
        }
        EnchantEntry current = currentEntry();
        int selection = current == null ? -1 : entries.indexOf(current);
        if (force || selection != sentSelection || !entries.equals(sentEntries)) {
            sentEntries = entries;
            sentSelection = selection;
            Services.PLATFORM.sendEntries(owner, new EnchantEntriesPayload(containerId, entries, selection));
        }
    }

    private void updateResult() {
        EnchantEntry entry = currentEntry();
        ItemStack input = inputs.getItem(INPUT_SLOT);
        result.setItem(0, entry != null && entry.isPaidBy(inputs.getItem(PAYMENT_SLOT))
                ? EnchantEntries.apply(input, entry.enchantment(), entry.level())
                : ItemStack.EMPTY);
    }

    @Nullable
    private EnchantEntry currentEntry() {
        if (selected != null) {
            for (EnchantEntry entry : entries) {
                if (entry.isSameOption(selected)) {
                    return entry;
                }
            }
        }
        return null;
    }

    private void takeResult() {
        EnchantEntry entry = currentEntry();
        if (entry != null) {
            inputs.getItem(PAYMENT_SLOT).shrink(entry.price().count());
        }
        inputs.setItem(INPUT_SLOT, ItemStack.EMPTY);
        if (villager != null) {
            villager.level().playSound(null, villager.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL,
                    1.0F, villager.getRandom().nextFloat() * 0.1F + 0.9F);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == RESULT_SLOT) {
            if (!moveItemStackTo(stack, INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, original);
        } else if (index == INPUT_SLOT || index == PAYMENT_SLOT) {
            if (!moveItemStackTo(stack, INVENTORY_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveToInputs(stack)) {
            boolean inventory = index < HOTBAR_START;
            if (!moveItemStackTo(stack, inventory ? HOTBAR_START : INVENTORY_START, inventory ? HOTBAR_END : HOTBAR_START, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }

    private boolean moveToInputs(ItemStack stack) {
        EnchantEntry entry = currentEntry();
        if (entry != null && stack.is(entry.price().item())) {
            return moveItemStackTo(stack, PAYMENT_SLOT, PAYMENT_SLOT + 1, false);
        }
        return !slots.get(INPUT_SLOT).hasItem() && moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != result && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public boolean stillValid(Player player) {
        return villager == null
                || villager.isAlive() && villager.getTradingPlayer() == player && EnchanterProfession.is(villager.getVillagerData());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (villager != null) {
            if (villager.getTradingPlayer() == player) {
                villager.setTradingPlayer(null);
            }
            clearContainer(player, inputs);
        }
    }

    private class ResultSlot extends Slot {
        ResultSlot(int x, int y) {
            super(result, 0, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            takeResult();
            super.onTake(player, stack);
        }
    }
}
