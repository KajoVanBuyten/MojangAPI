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
package net.andylizi.mojang.api.skin;

/**
 * 皮肤模型类型
 * @author andylizi
 */
public enum SkinModelType {
    /**
     * 标准Steve模型. 
     */
    CLASSIC(""),
    
    /**
     * Alex模型 (3像素手臂). 
     */
    SLIM("slim");
    
    private final String type;

    private SkinModelType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
