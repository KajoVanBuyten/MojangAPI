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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import static java.nio.charset.StandardCharsets.UTF_8;

import net.andylizi.mojang.exception.MojangException;
import net.andylizi.mojang.exception.NotSecuredException;
import net.andylizi.mojang.utils.IOUtils;
import static net.andylizi.mojang.utils.Common.*;

/**
 * 皮肤API. 
 * 
 * @author andylizi
 */
public class SkinAPI {
    private static final String SKIN_URL = "https://api.mojang.com/user/profile/%s/skin";

    /**
     * Mojang账户UUID
     */
    private String uid;
    
    /**
     * AccessToken
     */
    private transient String accessToken;

    /**
     * @param uid Mojang账户UUID(不带'-')
     * @param accessToken 有效的AccessToken
     */
    public SkinAPI(String uid, String accessToken) {
        this.uid = uid;
        this.accessToken = accessToken;
    }

    /**
     * 更新UUID. 
     * @param uid 新UUID(不带'-')
     */
    public void updateUID(String uid) {
        this.uid = Objects.requireNonNull(uid);
    }

    /**
     * 更新AccessToken. 
     * 
     * @param accessToken 新AccessToken
     */
    public void updateAccessToken(String accessToken) {
        this.accessToken = Objects.requireNonNull(accessToken);
    }

    /**
     * 上传皮肤. 
     * 
     * @param data PNG皮肤文件数据
     * @param type 皮肤类型
     * @return 是否成功
     * @throws java.io.IOException 发生 I/O 错误
     * @throws net.andylizi.mojang.exception.NotSecuredException 需要验证安全问题
     * @throws net.andylizi.mojang.exception.MojangException Mojang服务器返回异常
     */
    public boolean uploadSkin(byte[] data, SkinModelType type) throws IOException, NotSecuredException, MojangException{
        Objects.requireNonNull(data);
        Objects.requireNonNull(type);
        
        URL url = new URL(String.format(SKIN_URL, uid));
        final String boundary = Common.createBoundary();
        HttpURLConnection conn = Common.createHttpURLConnection(url);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Accept-Encoding", "gzip");
        conn.setRequestProperty("Authorization ", String.format(AUTHORIZATION_PROPERTY_FORMAT, accessToken));
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=".concat(boundary));
        conn.connect();
        
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), UTF_8))) {
            writer.write("--".concat(boundary).concat("\r\n"));
            writer.write("Content-Disposition: form-data; name=\"model\"\r\n");
            writer.write("\r\n");
            writer.write(type.toString().concat("\r\n"));
            writer.write("--".concat(boundary).concat("\r\n"));
            writer.write("Content-Disposition: form-data; name=\"file\"; filename=\"dummy.png\"\r\n");
            writer.write("Content-Type: image/png\r\n");
            writer.write("\r\n");
            writer.flush();
            conn.getOutputStream().write(data);
            writer.write("\r\n--".concat(boundary).concat("--"));
        }

        Common.throwMojangException(new String(IOUtils.readFully(IOUtils.handleInputStream(conn)), UTF_8));
        return conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT;
    }
    
    /**
     * 上传皮肤. 
     * 
     * @param in PNG皮肤文件的输入流
     * @param type 皮肤类型
     * @return 是否成功
     * @throws java.io.IOException 发生 I/O 错误
     * @throws net.andylizi.mojang.exception.NotSecuredException 需要验证安全问题
     * @throws net.andylizi.mojang.exception.MojangException Mojang服务器返回异常
     */
    public boolean uploadSkin(InputStream in, SkinModelType type) throws IOException, NotSecuredException, MojangException{
        return uploadSkin(IOUtils.readFully(in), type);
    }
    
    /**
     * 上传皮肤. 
     * 
     * @param file PNG皮肤文件
     * @param type 皮肤类型
     * @return 是否成功
     * @throws java.io.FileNotFoundException 文件不存在
     * @throws java.io.IOException 发生 I/O 错误
     * @throws net.andylizi.mojang.exception.NotSecuredException 需要验证安全问题
     * @throws net.andylizi.mojang.exception.MojangException Mojang服务器返回异常
     */
    public boolean uploadSkin(File file, SkinModelType type) throws FileNotFoundException, IOException, NotSecuredException, MojangException{
        return uploadSkin(new BufferedInputStream(new FileInputStream(file)), type);
    }
    
    public boolean resetSkin() throws IOException, NotSecuredException, MojangException{
        URL url = new URL(String.format(SKIN_URL, uid));
        HttpURLConnection conn = Common.createHttpURLConnection(url);
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Accept-Encoding", "gzip");
        conn.setRequestProperty("Authorization ", String.format(AUTHORIZATION_PROPERTY_FORMAT, accessToken));
        conn.connect();
        
        if(conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT)
            return true;
        Common.throwMojangException(new String(IOUtils.readFully(IOUtils.handleInputStream(conn)), UTF_8));
        return false;
    }
    
    /**
     * 渲染出2D的皮肤预览图.
     * 
     * @param skin 皮肤
     * @param modelType 皮肤类型
     * @return 2D皮肤预览图 (16*32像素)
     * @throws java.lang.IllegalArgumentException 皮肤大小不正确. 
     */
    public static BufferedImage renderSkinPreview2D(BufferedImage skin, SkinModelType modelType) throws IllegalArgumentException{
        if (!checkSkinSize(skin)) 
            throw new IllegalArgumentException("Invalid image size: " + skin.getWidth() + "*" + skin.getHeight());
        boolean slim = modelType == SkinModelType.SLIM;

        BufferedImage preview = new BufferedImage(16, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = preview.createGraphics();
        drawImage(g2d, skin.getSubimage(8, 8, 8, 8), 4, 0); //head
        drawImage(g2d, skin.getSubimage(20, 20, 8, 12), 4, 8); //body
        drawImage(g2d, skin.getSubimage(44, 20, (slim ? 3 : 4), 12), (slim ? 1 : 0), 8); //right arm
        drawImage(g2d, skin.getSubimage(44, 20, (slim ? 3 : 4), 12), 12, 8); //left arm
        drawImage(g2d, skin.getSubimage(4, 20, 4, 12), 4, 20); //left leg
        drawImage(g2d, skin.getSubimage(4, 20, 4, 12), 8, 20); //right leg

        drawImage(g2d, skin.getSubimage(40, 8, 8, 8), 4, 0); //hat
        if (skin.getHeight() == 64) {
            drawImage(g2d, skin.getSubimage(36, 52, (slim ? 3 : 4), 12), 12, 8); //left arm
            drawImage(g2d, skin.getSubimage(20, 52, 4, 12), 8, 20); //left leg
            
            drawImage(g2d, skin.getSubimage(20, 20 + 16, 8, 12), 4, 8); //body wear
            drawImage(g2d, skin.getSubimage(52, 20 + 16 + 16, (slim ? 3 : 4), 12), 12, 8); //left arm wear
            drawImage(g2d, skin.getSubimage(44, 20 + 16, (slim ? 3 : 4), 12), (slim ? 1 : 0), 8); //right arm wear
            drawImage(g2d, skin.getSubimage(4, 52, 4, 12), 8, 20); //left leg wear
            drawImage(g2d, skin.getSubimage(4, 20 + 16, 4, 12), 4, 20); //right leg wear
        }

        g2d.dispose();
        return preview;
    }
    
    private static Graphics2D drawImage(Graphics2D g, Image image, int x, int y) {
        g.drawImage(image, x, y, null);
        return g;
    }
    
    public static boolean checkSkinSize(Image image) {
        return image.getWidth(null) == 64 && (image.getHeight(null) == 64 || image.getHeight(null) == 32);
    }
}
