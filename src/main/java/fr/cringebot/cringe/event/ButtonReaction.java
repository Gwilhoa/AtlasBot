package fr.cringebot.cringe.event;

import fr.cringebot.cringe.CommandBuilder.Gift;
import fr.cringebot.cringe.CommandBuilder.Inventory;
import fr.cringebot.cringe.CommandBuilder.Shop;
import fr.cringebot.cringe.escouades.Squads;
import fr.cringebot.cringe.objects.Item;
import fr.cringebot.cringe.waifus.InvWaifu;
import fr.cringebot.cringe.waifus.ListWaifu;
import fr.cringebot.cringe.waifus.Waifu;
import fr.cringebot.cringe.waifus.WaifuCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class ButtonReaction {
	public static void onButton(ButtonInteractionEvent event) throws InterruptedException {
		if (event.getButton().getId().startsWith("gift")) {
			if (event.getMember().getId().equals(event.getMessage().getEmbeds().get(0).getFooter().getText()))
				Gift.openGift(event);
			else
				event.reply("on vole pas le cadeau des autres").setEphemeral(true).queue();
		} else if (event.getButton().getId().startsWith("inv1")) {
			Inventory.setCollectionInv(event);
		} else if (event.getButton().getId().startsWith("inv0")) {
			if (event.getButton().getId().split("_")[1].equals(event.getMember().getId()))
				event.editMessageEmbeds(Inventory.getInventory(event.getMember()).build()).setActionRow(new ButtonImpl("inv1_"+event.getMember().getId(), "Ouvrir le sac de jetons", ButtonStyle.SUCCESS, false, null)).queue();
			else
				event.reply("tu es pas la personne attendu").setEphemeral(true).queue();
		} else if (event.getButton().getId().startsWith("shop")) {
			String id = event.getButton().getId().substring("shop_".length());
			if (!event.getMember().getId().equals(id.split(";")[0]))
			{
				event.reply("tu es pas la personne attendu").setEphemeral(true).queue();
				return;
			}
			String item = id.split(";")[1];
			if (item.equals("stop"))
			{
				event.editMessageEmbeds(Shop.ShopDisplay(event.getMember()).build()).setActionRow(Shop.PrincipalMenu(event.getMember())).queue();
				event.reply("opération annulé").setEphemeral(true).queue();
				return;
			}
			int prix = Integer.parseInt(id.split(";")[2]);
			int amount = Integer.parseInt(id.split(";")[3]);
			if (event.getButton().getLabel().equalsIgnoreCase("acheter")) {
				Shop.buy(event.getMember(), item, prix, amount);
				event.editMessageEmbeds(Shop.ShopDisplay(event.getMember()).build()).setActionRow(Shop.PrincipalMenu(event.getMember())).queue();
				event.getChannel().sendMessage(event.getMember().getAsMention() + ", tu as acheté " + amount + " " + item).queue();
			} else {
				Shop.panelamount(event.getMember(), item, prix, amount, event);
			}
		} else if (event.getButton().getId().startsWith("trade")) {
			EmbedBuilder eb = new EmbedBuilder().setTitle("Requête d'échange").setDescription(event.getMessage().getEmbeds().get(0).getDescription());
			ArrayList<ButtonImpl> bttn = new ArrayList<>();
			bttn.add(new ButtonImpl("trade_ok", "accepter", ButtonStyle.SUCCESS, true, null));
			bttn.add(new ButtonImpl("trade_no", "refuser", ButtonStyle.DANGER, true, null));
			if (event.getButton().getId().contains("ok")) {
				if (!event.getMember().getId().equals(event.getButton().getId().split(";")[4]))
					event.reply("tu n'es pas la personne attendu").setEphemeral(true).queue();
				Member sender = event.getGuild().getMemberById(event.getButton().getId().split(";")[3]);
				Member receiver = event.getGuild().getMemberById(event.getButton().getId().split(";")[4]);
				InvWaifu invWaifuSender = Squads.getstats(sender).popInvWaifu(Integer.parseInt(event.getButton().getId().split(";")[1]));
				InvWaifu invWaifuReceiver = Squads.getstats(receiver).popInvWaifu(Integer.parseInt(event.getButton().getId().split(";")[2]));
				Squads.getstats(sender).addInvWaifu(invWaifuReceiver);
				Squads.getstats(receiver).addInvWaifu(invWaifuSender);
				String Origin  = invWaifuSender.getWaifu().getOrigin();
				TradeCompleteCollection(eb, receiver, Origin, receiver.getGuild());
				Origin = invWaifuReceiver.getWaifu().getOrigin();
				TradeCompleteCollection(eb, sender, Origin, receiver.getGuild());
				event.editMessageEmbeds(eb.setColor(Color.green).setFooter("accepté").build()).setActionRow(bttn).queue();
				event.getChannel().sendMessage("échange accepté").reference(event.getMessage()).queue();
			} else {
				if (!event.getMember().getId().equals(event.getButton().getId().split(";")[1]))
					event.reply("tu n'es pas la personne attendu").setEphemeral(true).queue();
				event.editMessageEmbeds(eb.setColor(Color.red).setFooter("refusé").build()).setActionRow(bttn).queue();
				event.getChannel().sendMessage("échange refusé").reference(event.getMessage()).queue();
			}
		}
		else if (event.getButton().getId().startsWith("USECE")) {
			if (!event.getButton().getId().split(";")[1].equals(event.getMember().getId()))
				event.reply("tu es pas la personne attendu").setEphemeral(true).queue();
			else if (Squads.getstats(event.getMember()).getAmountItem(Item.Items.CE.getStr()) <= 0)
				event.reply("tu as pas de chronometre érotique").queue();
			else {
				if (!Squads.getstats(event.getMember()).removeTime(1800000L)) {
					event.reply("ça a pas marché").setEphemeral(true).queue();
					return;
				}
				Squads.getstats(event.getMember()).removeItem(Item.Items.CE.getStr());
				EmbedBuilder eb = WaifuCommand.capturedWaifu(event.getMember().getId(), event.getGuild());
				if (!Objects.equals(eb.build().getColor(), Color.black) && !Objects.equals(eb.build().getColor(), Color.WHITE))
					event.editMessageEmbeds(eb.build()).setActionRow(new ButtonImpl("USECE;"+event.getMember().getId(), "utiliser un Chronomètre érotique", ButtonStyle.SUCCESS,true, null)).queue();
				else
				{
					if (Squads.getstats(event.getMember()).getAmountItem(Item.Items.CE.getStr()) > 0)
						event.editMessageEmbeds(eb.build()).setActionRow(new ButtonImpl("USECE;"+event.getMember().getId(), "utiliser un Chronomètre érotique", ButtonStyle.SUCCESS,false, null)).queue();
					else
						event.editMessageEmbeds(eb.build()).setActionRow(new ButtonImpl("USECE;"+event.getMember().getId(), "utiliser un Chronomètre érotique", ButtonStyle.SUCCESS,true, null)).queue();
				}
			}
		}
		else if (event.getButton().getId().startsWith("harem")){
			WaifuCommand.haremEmbed(event,Integer.parseInt(event.getButton().getId().substring("harem_".length()).split(";")[1]), event.getButton().getId().substring("harem_".length()).split(";")[0]);
		} else if (event.getButton().getId().startsWith("list_"))
		{
			String memId = event.getButton().getId().substring("list_".length()).split(";")[0];
			int page = Integer.parseInt(event.getButton().getId().substring("list_".length()).split(";")[1]);
			String key = event.getButton().getId().substring("list_".length()).split(";")[2];
			event.editMessageEmbeds(ListWaifu.listwaifu(event.getGuild(), memId, key, page).build()).setActionRows(WaifuCommand.generateButtonList(memId, key, page)).queue();
		} else {
			event.reply("coming soon").setEphemeral(true).queue();
		}
	}

	private static void TradeCompleteCollection(EmbedBuilder eb, Member receiver, String origin, Guild guild) {
		if (Squads.getstats(receiver).isCompleteCollection(origin) && Squads.getstats(receiver).CompleteCollection(origin)) {
			Integer pts = Waifu.getWaifusByOrigin(origin).size();
			pts = pts*100/2;
			eb.appendDescription("\nFélicitation, "+receiver.getAsMention()+" tu as finis la collection\n"+pts+" points pour "+ Squads.getSquadByMember(receiver).getSquadRole(guild).getAsMention());

			Squads.getstats(receiver).addPoint(pts.longValue());
		}
	}
}
