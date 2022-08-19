package fr.cringebot.cringe.event;

import fr.cringebot.cringe.CommandBuilder.Shop;
import fr.cringebot.cringe.Polls.PollListener;
import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.objects.SelectOptionImpl;
import fr.cringebot.cringe.reactionsrole.MessageReact;
import fr.cringebot.cringe.reactionsrole.RoleReaction;
import fr.cringebot.cringe.waifus.Waifu;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static fr.cringebot.cringe.cki.mainCommand.MenuInteract;

public class MenuInteract {
	public static void onSelectMenu(SelectMenuInteractionEvent event) throws ExecutionException, InterruptedException {
		if (event.getSelectMenu().getId().startsWith("collection")){
			Shop.CollecSelecMenu(event);
		}
		if (event.getSelectMenu().getId().startsWith("shop")) {
			Shop.ShopSelectMenu(event);
		}
		if (!event.getMessage().getEmbeds().isEmpty() && event.getMessage().getEmbeds().get(0).getAuthor() != null && event.getMessage().getEmbeds().get(0).getAuthor().getName().equals("poll")) {
			PollListener.reactSelectMenu(event.getMessage(), event.getMember(), event.getSelectedOptions().get(0));
			event.reply("Ton vote a été enregistré \uD83D\uDC4D").setEphemeral(true).queue();
		}
		if (event.getComponent().getId().equals("Waifu")) {
			if (event.getSelectedOptions().get(0).getValue().equals("stop"))
				event.getMessage().delete().queue();
			new Waifu(event.getMessage().getAttachments().get(0), event.getMessage().getEmbeds().get(0).getTitle(), event.getMessage().getEmbeds().get(0).getDescription(), event.getSelectedOptions().get(0).getLabel(), event.getMessage().getEmbeds().get(0).getFooter().getText(), false);
			Squads.addPoints(event.getMember(), 1000L);
			event.getMessage().delete().queue();
		}
		if (event.getComponent().getId().equals("cki"))
			MenuInteract(event.getSelectedOptions().get(0).getValue(), event.getMessage());
		if (event.getComponent().getId().equals("role")) {
			String[] args = event.getMessage().getEmbeds().get(0).getDescription().split("\n");
			for (MessageReact mr : MessageReact.message)
				if (mr.getTitle().equals(event.getSelectedOptions().get(0).getLabel())) {
					mr.addRole(new RoleReaction(args[0], event.getMessage().getEmbeds().get(0).getFooter().getText(), args[1]), event.getGuild());
					MessageReact.save();
				}
			event.getMessage().delete().queue();
		}
	}
}
