package fr.cringebot.cringe.event;

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
			if (!event.getMember().getId().equals(event.getMessage().getContentRaw().split("\n")[1]))
			{
				event.reply("tu es pas la personne attendu").setEphemeral(true).queue();
				return;
			}
			if (event.getSelectedOptions().get(0).getValue().equals("next"))
			{
				event.reply("oui").complete().deleteOriginal().queue();
				int nb = (Integer.parseInt(event.getSelectMenu().getId().substring("collection".length())) + 1) * 10;
				int i = nb;
				ArrayList<SelectOption> options = new ArrayList<>();
				while (i < nb + 10)
				{
					options.add(new SelectOptionImpl(Waifu.getAllOrigins().get(i), Waifu.getAllOrigins().get(i)));
					i++;
				}
				options.add(new SelectOptionImpl("autres", "next"));
				SelectMenuImpl selectionMenu = new SelectMenuImpl("collection"+Integer.parseInt(event.getSelectMenu().getId().substring("collection".length())) + 1, "selectionnez un choix", 1, 1, false, options);
				event.getMessage().editMessage(event.getMessage().getContentRaw()).setActionRow(selectionMenu).queue();
			}
			else {
				if (event.getSelectedOptions().get(0).getValue().equals("B2K") || Squads.getstats(event.getMember()).getWaifubyOrigin(event.getSelectedOptions().get(0).getValue()).size() == Waifu.getWaifusByOrigin(event.getSelectedOptions().get(0).getValue()).size())
				{
					event.getMessage().delete().queue();
					event.reply("désolé j'en ai plus").queue();
					return;
				}
				Squads.getstats(event.getMember()).addCollection(event.getSelectedOptions().get(0).getValue(), event.getMessage());
				event.getMessage().delete().queue();
				event.reply("tu as acheté un jeton "+ event.getSelectedOptions().get(0).getValue() + "\ntu en as " + Squads.getstats(event.getMember()).getCollection().get(event.getSelectedOptions().get(0).getValue())).queue();
				Squads.getstats(event.getMember()).removeCoins(10L);
			}
		}
		if (event.getSelectMenu().getId().equals("shop")) {
			if (event.getSelectedOptions().get(0).getValue().equals("stop")) {
				event.getMessage().delete().queue();
				event.reply("merci, à bientot").setEphemeral(true).queue();
			}
			else if (event.getSelectedOptions().get(0).getValue().equals("PDCFU"))
			{
				if (Squads.getstats(event.getMember()).getCoins() < 10)
					event.reply("tu as pas assez d'argent").setEphemeral(true).queue();
				else {
					ArrayList<SelectOption> options = new ArrayList<>();
					int i = 0;
					while (10 > i)
					{
						options.add(new SelectOptionImpl(Waifu.getAllOrigins().get(i), Waifu.getAllOrigins().get(i)));
						i++;
					}
					options.add(new SelectOptionImpl("autres", "next"));
					SelectMenuImpl selectionMenu = new SelectMenuImpl("collection0", "selectionnez un choix", 1, 1, false, options);
					event.reply("de quelle collection votre pièce ?\n"+event.getMember().getId())
							.addActionRow(selectionMenu).queue();
				}
			} else if (event.getSelectedOptions().get(0).getValue().equals("RDTPFU")){
				if (Squads.getstats(event.getMember()).getCoins() >= 25L)
				{
					if (Waifu.removeTime(event.getMember().getId(), 1800000L))
					{
						event.reply("votre chronomètre a fait effet, je ressens une excitation").queue();
						Squads.getstats(event.getMember()).removeCoins(25L);
					} else {
						event.reply("tiens ça a pas marché, réessayez plus tard").queue();
					}
				} else
				{
					event.reply("tu as pas assez d'argent").setEphemeral(true).queue();
				}
			}
			else {
				event.reply("coming soon").setEphemeral(true).queue();
			}
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
