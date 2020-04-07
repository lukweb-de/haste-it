package de.lukweb.discordbeam.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.DocumentAdapter;
import de.lukweb.share.ShareWebTools;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;

public class WebhookValidator {

    private JTextComponent textComponent;

    public WebhookValidator(JTextComponent textComponent) {
        this.textComponent = textComponent;
    }

    public ValidationInfo validate() {
        String url = textComponent.getText().trim();
        if (url.isEmpty()) {
            return null;
        }

        String urlCheck = ShareWebTools.checkUrl(url);

        if (urlCheck != null) {
            return new ValidationInfo(urlCheck, textComponent);
        }

        if (!url.startsWith("https://discordapp.com/api/webhooks/")) {
            String message = "A Discord webhook should start with 'https://discordapp.com/api/webhooks/'";
            return new ValidationInfo(message, textComponent).asWarning();
        }

        return null;
    }

    public static void installOn(Disposable disposable, JTextComponent textComponent) {
        WebhookValidator validator = new WebhookValidator(textComponent);

        new ComponentValidator(disposable)
                .withValidator(validator::validate)
                .andStartOnFocusLost()
                .installOn(textComponent);

        DocumentAdapter listener = new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                ComponentValidator.getInstance(textComponent).ifPresent(ComponentValidator::revalidate);
            }
        };

        textComponent.getDocument().addDocumentListener(listener);
        Disposer.register(disposable, () -> {
            if (textComponent.getDocument() != null) {
                textComponent.getDocument().removeDocumentListener(listener);
            }
        });
    }
}
