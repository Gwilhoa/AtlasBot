/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   SimpleCommand.java                                 :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 12:44:32 by gchatain          #+#    #+#             */
/*   Updated: 2022/06/19 11:22:52 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.atlas.builder;

import fr.atlas.builder.Command.ExecutorType;
import net.dv8tion.jda.api.Permission;

import java.lang.reflect.Method;


public final class SimpleCommand {

    private final String name, description;
    private final ExecutorType executorType;
    private final Object object;
    private final Method method;
    private final Permission permission;

    private final String type;

    public SimpleCommand(String name, String description, ExecutorType executorType, Object object, Method method, Permission permission, String type) {
        super();
        this.name = name;
        this.description = description;
        this.executorType = executorType;
        this.object = object;
        this.method = method;
        this.permission = permission;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public Permission getPermission() {
        return permission;
    }

}