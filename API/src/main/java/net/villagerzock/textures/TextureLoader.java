package net.villagerzock.textures;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;

public class TextureLoader {
    private static final Map<String,Texture> cache = new HashMap<>();
    public Texture getImage(String s,Class<?> resourceClass){
        if (cache.containsKey(s))
            return cache.get(s);
        BufferedImage image = null;

// Versuche zuerst aus dem Classpath zu laden
        try (InputStream is = resourceClass.getResourceAsStream("/" + s)) {
            if (is != null) {
                image = ImageIO.read(is);
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Laden aus Ressourcen: " + e.getMessage());
        }

// Wenn das fehlschlägt, versuche direkt aus dem Dateisystem
        if (image == null) {
            try {
                System.out.println("Had to Read Textures from Source");
                image = ImageIO.read(new File("Server/src/main/resources/" + s));
            } catch (IOException e) {
                throw new RuntimeException("Konnte Textur nicht laden: " + s, e);
            }
        }
        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];
        image.getRGB(0,0,width,height,pixels,0,width);

        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(width * height * 4);

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                int pixel = pixels[y * width + x];
                byteBuffer.put((byte) ((pixel >> 16) & 0xFF));
                byteBuffer.put((byte) ((pixel >> 8) & 0xFF));
                byteBuffer.put((byte) (pixel & 0xFF));
                byteBuffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        byteBuffer.flip();

        int textureId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D,textureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA8,width,height,0,GL_RGBA,GL_UNSIGNED_BYTE,byteBuffer);

        glBindTexture(GL_TEXTURE_2D,0);
        Texture texture = new Texture(textureId,width,height);
        cache.put(s,texture);
        return texture;
    }
}
