package net.ixdarklord.packmger.client.button;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;
import java.util.Optional;

public abstract class ButtonBase {
    protected abstract void initializeButton(Object screenEvent);
    protected abstract void buttonFunction();
    protected abstract void updateButton();
    void moveButtonsLayout(List<?> listeners, int splitAt) {
        for (var widget : listeners) {
            if (widget instanceof Button button && !(button instanceof PlainTextButton)) {
                if (splitAt <= button.y) {
                    button.y += 12;
                } else {
                    button.y -= 12;
                }
            }
        }
    }
    public Optional<Button> getButton(List<?> widgets, String s) {
        for (var widget : widgets) {
            if (widget instanceof Button button && this.isContainsKey(button, s)) {
                return Optional.of(button);
            }
        }
        return Optional.empty();
    }
    private boolean isContainsKey(Button button, String key) {
        final Component message = button.getMessage();
        return message instanceof TranslatableComponent contents && contents.getKey().equals(key);
    }
}
