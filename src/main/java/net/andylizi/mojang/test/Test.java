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
package net.andylizi.mojang.test;

import java.io.File;
import java.util.Scanner;
import javax.imageio.ImageIO;

import net.andylizi.mojang.api.security.SecurityAPI;
import net.andylizi.mojang.api.security.SecurityQuestions;
import net.andylizi.mojang.api.skin.SkinAPI;
import net.andylizi.mojang.api.skin.SkinModelType;

/**
 * 测试类
 * @author andylizi
 */
public class Test {
    public static void main(String[] args){
        final String uid = "160509efc59b4f3d88e27cf40b2b5c19";
        final String token = "******[REDACTED]******";
        final File skinFile = new File("D:\\Steve.png");
        final SkinModelType type = SkinModelType.CLASSIC;
        try{
            ImageIO.write(SkinAPI.renderSkinPreview2D(ImageIO.read(skinFile), type), "png", new File("D:\\render.png"));

            SkinAPI skinAPI = new SkinAPI(uid, token);
            SecurityAPI securityAPI = new SecurityAPI(token);
            if(securityAPI.needSecurityQuestions()){
                SecurityQuestions questions = securityAPI.fetchSecurityQuestions();
                try (Scanner scanner = new Scanner(System.in)) {
                    System.out.println(questions.getQuestions()[0].question);
                    questions.answer(0, scanner.nextLine());
                    System.out.println(questions.getQuestions()[1].question);
                    questions.answer(1, scanner.nextLine());
                    System.out.println(questions.getQuestions()[2].question);
                    questions.answer(2, scanner.nextLine());
                }
                securityAPI.submitSecurityQuestions(questions);
            }
            //skinAPI.resetSkin();
            skinAPI.uploadSkin(skinFile, type);
        }catch(Throwable t){
            t.printStackTrace();
        }
    }
}
