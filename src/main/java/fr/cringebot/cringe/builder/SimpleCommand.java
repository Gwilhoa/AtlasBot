/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   SimpleCommand.java                                 :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 12:44:32 by gchatain          #+#    #+#             */
/*   Updated: 2022/03/23 16:15:23 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.builder;

import fr.cringebot.cringe.builder.Command.ExecutorType;

import java.lang.reflect.Method;


public final class SimpleCommand {

    private final String name, description;
    private final ExecutorType executorType;
    private final Object object;
    private final Method method;

    public SimpleCommand(String name, String description, ExecutorType executorType, Object object, Method method) {
        super();
        this.name = name;
        this.description = description;
        this.executorType = executorType;
        this.object = object;
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ExecutorType getExecutorType() {
        return executorType;
    }

    public Object getObject() {
        return object;
    }

    public Method getMethod() {
        return method;
    }

}