package fr.cringebot.cringe.objects;

import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.jetbrains.annotations.NotNull;

public class SelectOptionImpl extends SelectOption {
    public SelectOptionImpl(@NotNull String label, String value) {
        super(label, value);
    }
}
