package org.github.discord.onoriobot;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

public class OnorioSimpleBot {
    public static void main(String[] args) {
        DiscordClient client = DiscordClient.create(args[0]);

        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
            // ReadyEvent example
            Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event ->
                            Mono.fromRunnable(() -> {
                                final User self = event.getSelf();
                                System.out.println("Bot " + self.getUsername() + " has logged in Succesfully");
                            }))
                    .then();

            // MessageCreateEvent example
            Mono<Void> handlePingCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();

                if (message.getContent().equalsIgnoreCase("!ping")) {
                    System.out.println("El autor es: " + message.getAuthor().map(User::getUsername).stream().findFirst());
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage("pong!"));
                }

                if (message.getContent().equalsIgnoreCase("!hola")) {
                    System.out.println("Message Send By: " + message.getAuthor().map(User::getUsername).stream().findFirst());
                    return message.getChannel().flatMap(channel -> channel.createMessage("Ola sou Onorio, encando de conhecer"));
                }

                return Mono.empty();
            }).then();

            // combine them!
            return printOnLogin.and(handlePingCommand);
        });
        login.block();


    }

}
