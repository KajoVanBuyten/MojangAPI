/*
 * Copyright (C) 2016 andylizi.
 * 
 * This library is free software: you can redistribute it and/or  
 * modify it under the terms of the GNU Lesser General Public     
 * License as published by the Free Software Foundation, either   
 * version 3 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU Lesser General Public License for more details.            
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see 
 * <http://www.gnu.org/licenses/>.
 */
package net.andylizi.mojang.exception;

/**
 * 表示需要验证账户安全问题
 * @author andylizi
 */
public class NotSecuredException extends MojangException{
    private static final long serialVersionUID = 7628877917934878403L;

    public NotSecuredException() {
        super("Forbidden","Current IP not secured");
    }
    
}
