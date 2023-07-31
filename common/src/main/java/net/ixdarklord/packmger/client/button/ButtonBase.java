package net.ixdarklord.packmger.client.button;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.List;
import java.util.Optional;

public abstract class ButtonBase {
    protected abstract void initializeButton(Object screenEvent);
    protected abstract void buttonFunction();
    protected abstract void updateButton();
    void moveButtonsLayout(List<?> listeners, int splitAt) {
        for (var widget : listeners) {
            if (widget instanceof Button button && !(button instanceof PlainTextButton)) {
                if (splitAt <= button.getY()) {
                    button.setY(button.getY() + 12);
                } else {
                    button.setY(button.getY() - 12);
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
        final ComponentContents message = button.getMessage().getContents();
        return message instanceof TranslatableContents contents && contents.getKey().equals(key);
    }
}
