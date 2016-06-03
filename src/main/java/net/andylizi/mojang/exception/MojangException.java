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

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * Mojang服务器异常. 
 * 
 * 常见错误类型:
 * <table border=1>
 *  <tr>
 *      <th>HTTP状态码</th>
 *      <th>错误内容</th>
 *      <th>错误描述</th>
 *      <th>原因</th>
 *  </tr>
 *  <tr>
 *      <td>401 Unauthorized</td>
 *      <td>Unauthorized</td>
 *      <td>The request requires user authentication</td>
 *      <td>AccessToken错误</td>
 *  </tr>
 *  <tr>
 *      <td>403 Forbidden</td>
 *      <td>ForbiddenOperationException</td>
 *      <td>The request requires user authentication</td>
 *      <td>AccessToken错误</td>
 *  </tr>
 *  <tr>
 *      <td>400 Bad Request</td>
 *      <td>IllegalArgumentException</td>
 *      <td>Content is not an image</td>
 *      <td>传输内容不是PNG图片</td>
 *  </tr>
 *  <tr>
 *      <td>403 Forbidden</td>
 *      <td>Forbidden</td>
 *      <td>Current IP not secured</td>
 *      <td>当前IP未验证安全问题</td>
 *  </tr>
 * </table>
 * 
 * @author andylizi
 */
public class MojangException extends Exception{
    public static final Adapter ADAPTER = new Adapter();
    
    private String error;
    private String message;

    public MojangException(String error, String message) {
        super(error+": "+message);
        this.error = error;
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public String getErrorMessage() {
        return message;
    }
    
    public static class Adapter extends TypeAdapter<MojangException>{
        private Adapter(){};
        
        @Override
        public void write(JsonWriter writer, MojangException obj) throws IOException {
            writer
                    .beginObject()
                        .name("error").value(obj.error)
                        .name("errorMessage").value(obj.message)
                    .endObject();
        }

        @Override
        public MojangException read(JsonReader reader) throws IOException {
            String error = "undefine";
            String message = "undefine";
            reader.beginObject();
            while(reader.hasNext()){
                switch(reader.nextName()){
                    case "error":
                        error = reader.nextString();
                        break;
                    case "errorMessage":
                        message = reader.nextString();
                        break;
                }
            }
            reader.endObject();
            return new MojangException(error, message);
        }
        
    }
}
