package io.github.emberbocor.villagertradeenchant.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.emberbocor.villagertradeenchant.enchant.EnchantEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.core.Holder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

record EnchantRow(EnchantEntry entry, Component name, Component suffix, ItemStack price) {
    private static final String ELLIPSIS = "…";

    static EnchantRow of(EnchantEntry entry) {
        Holder<Enchantment> enchantment = entry.enchantment();
        MutableComponent name = enchantment.value().description().copy();
        ComponentUtils.mergeStyles(name, Style.EMPTY.withColor(enchantment.is(EnchantmentTags.CURSE) ? ChatFormatting.RED : ChatFormatting.WHITE));
        MutableComponent suffix = Component.empty();
        if (entry.level() != 1 || enchantment.value().getMaxLevel() != 1) {
            suffix.append(Component.empty()
                    .withStyle(name.getStyle())
                    .append(CommonComponents.SPACE)
                    .append(Component.translatableWithFallback("enchantment.level." + entry.level(), String.valueOf(entry.level()))));
        }
        return new EnchantRow(entry, name, suffix, new ItemStack(entry.price().item(), entry.price().count()));
    }

    Component fullName() {
        return Component.empty().append(name).append(suffix);
    }

    boolean fits(Font font, int maxWidth) {
        return font.width(FormattedText.composite(name, suffix)) <= maxWidth;
    }

    FormattedCharSequence fit(Font font, int maxWidth) {
        FormattedText full = FormattedText.composite(name, suffix);
        if (font.width(full) <= maxWidth) {
            return Language.getInstance().getVisualOrder(full);
        }
        Component ellipsis = Component.literal(ELLIPSIS).withStyle(name.getStyle());
        int nameWidth = maxWidth - font.width(suffix) - font.width(ellipsis);
        FormattedText text = nameWidth >= 0
                ? FormattedText.composite(head(font, name, nameWidth), ellipsis, suffix)
                : FormattedText.composite(head(font, full, maxWidth - font.width(ellipsis)), ellipsis);
        return Language.getInstance().getVisualOrder(text);
    }

    private static FormattedText head(Font font, FormattedText text, int maxWidth) {
        List<Segment> segments = new ArrayList<>();
        font.substrByWidth(text, Math.max(0, maxWidth)).visit((style, content) -> {
            segments.add(new Segment(style, content));
            return Optional.empty();
        }, Style.EMPTY);
        while (!segments.isEmpty() && segments.getLast().content().isBlank()) {
            segments.removeLast();
        }
        if (!segments.isEmpty()) {
            Segment last = segments.removeLast();
            segments.add(new Segment(last.style(), last.content().stripTrailing()));
        }
        return FormattedText.composite(segments.stream().map(segment -> FormattedText.of(segment.content(), segment.style())).toList());
    }

    private record Segment(Style style, String content) {
    }
}
