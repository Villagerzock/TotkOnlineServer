package net.villagerzock;

import imgui.*;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import javassist.bytecode.ByteArray;
import net.villagerzock.entities.Player;
import net.villagerzock.math.Quaternion;
import net.villagerzock.math.Vector3;
import net.villagerzock.stream.ModifiableDataInputStream;
import net.villagerzock.stream.TrackingPrintStream;
import net.villagerzock.textures.Texture;
import net.villagerzock.textures.TextureLoader;
import org.lwjgl.system.linux.XDestroyWindowEvent;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class ImGuiServerHandler extends Application {
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private PipedOutputStream in = new PipedOutputStream();
    private final PipedInputStream pin = new PipedInputStream();

    @Override
    protected void configure(Configuration config) {
        config.setTitle("Totk Online Server | Port: 1027");
    }



    public void setupConsole() {
        try {
            pin.connect(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.setIn(pin);
        Server.originalPrintStream = System.out;
        TrackingPrintStream stream = TrackingPrintStream.create(baos);
        System.setOut(stream);
    }

    @Override
    protected void postRun() {
        Server.getInstance().shutdown();
    }

    @Override
    protected void initImGui(Configuration config) {
        super.initImGui(config);

        ImGuiIO io = ImGui.getIO();
        File file = new File("layouts/default.ini");
        if (!file.getParentFile().exists()){
            file.mkdirs();
        }
        if (!file.exists()){
            try (InputStream is = getClass().getResourceAsStream("layouts/default.ini" )) {
                if (is != null) {

                    file.createNewFile();
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(Arrays.toString(is.readAllBytes()));
                }
            } catch (IOException e) {
                System.err.println("Fehler beim Laden aus Ressourcen: " + e.getMessage());
            }
        }
        Path def = Paths.get("layouts/default.ini");
        Path layouts = Paths.get("layouts/custom.ini");

        try {
            Files.copy(def,layouts, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        io.setIniFilename(layouts.toFile().getAbsolutePath());

        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        TextureLoader textureLoader = new TextureLoader();
        textureLoader.getImage("depths.png",getClass());
        textureLoader.getImage("surface.png",getClass());
        textureLoader.getImage("sky.png",getClass());
    }
    private int mapData = 1;
    private float zoom = 1.0f;
    private ImBoolean showPreferences = new ImBoolean(false);
    private String nextLine = "";
    @Override
    public void process() {
        setupDockspace();
        if (ImGui.begin("Console")){
            ImGui.beginChild("Console",ImGui.getWindowSizeX(),ImGui.getWindowSizeY() - 60);
            boolean changed = ((TrackingPrintStream)System.out).hasChanged();
            System.out.flush();
            renderAnsiText(baos.toString(StandardCharsets.UTF_8));
            if (changed){
                Server.originalPrintStream.println("System Out Changed");
                ImGui.setScrollHereY(1.0f);
            }
            ImGui.endChild();
            ImString input = new ImString();
            ImGui.setNextItemWidth(-1);
            if (ImGui.inputText("##InputConsole",input, ImGuiInputTextFlags.EnterReturnsTrue)){
                try {
                    in.write(input.get().getBytes(StandardCharsets.UTF_8));
                    in.close();
                    in = new PipedOutputStream();
                    pin.connect(in);
                    System.out.println(input.get());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        ImGui.end();
        if (ImGui.begin("Map")){

            if (ImGui.button("Depths")){
                mapData = 0;
            }
            ImGui.sameLine();
            if (ImGui.button("Surface")){
                mapData = 1;
            }
            ImGui.sameLine();
            if (ImGui.button("Sky")){
                mapData = 2;
            }
            Texture tex = new TextureLoader().getImage(mapData == 0 ? "depths.png" : mapData == 1 ? "surface.png" : "sky.png",getClass());
            ImVec2 windowSize = ImGui.getWindowSize();


            float imgWidth = tex.width();
            float imgHeight = tex.height();


            float windowAspect = windowSize.x / windowSize.y;
            float imageAspect = imgWidth / imgHeight;


            float drawWidth, drawHeight;

            if (imageAspect > windowAspect) {

                drawWidth = windowSize.x;
                drawHeight = drawWidth / imageAspect;
            } else {
                // Bild ist höher oder quadratischer → volle Fensterhöhe nutzen
                drawHeight = windowSize.y - 60;
                drawWidth = drawHeight * imageAspect;
            }


            if (ImGui.beginChild("mapRenderer",ImGuiWindowFlags.AlwaysHorizontalScrollbar | ImGuiWindowFlags.AlwaysVerticalScrollbar)){

                if (ImGui.isWindowHovered() && ImGui.isKeyDown(ImGuiKey.LeftCtrl)) {
                    float wheel = ImGui.getIO().getMouseWheel();
                    if (wheel != 0) {
                        zoom += wheel * 0.1f;
                        if (zoom < 0.1f) zoom = 0.1f;
                        if (zoom > 10f) zoom = 10f;
                    }
                }
                float scaledWidth = drawWidth * zoom;
                float scaledHeight = drawHeight * zoom;

// horizontal und vertikal zentrieren
                float cursorPosX = (windowSize.x - drawWidth) / 2f;



                ImGui.setCursorPosX(cursorPosX);
                ImGui.image(tex.id(),drawWidth,drawHeight);

                for (Player player : Server.getInstance().getEntityHandler().getPlayers()){
                    drawPlayerOnMap(player,drawWidth,drawHeight);
                }
            }
            ImGui.endChild();

            if (showPreferences.get()){
                if (ImGui.begin("Preferences",showPreferences)){
                    if (ImGui.beginTabBar("preferencesTabBar")){
                        if (ImGui.beginTabItem("System")){
                            ImGui.endTabItem();
                        }
                        ImGui.endTabBar();
                    }
                }
                ImGui.end();
            }

        }
        ImGui.end();
        if (ImGui.begin("Player Manager")){
            if(ImGui.beginTabBar("player_manager")){
                if (ImGui.beginTabItem("Player List")){
                    for (Player player : Server.getInstance().getEntityHandler().getPlayers()){
                        renderUserButton(player);
                    }
                    ImGui.endTabItem();
                }
                if (ImGui.beginTabItem("Ban List")) {
                    ImGui.endTabItem();
                }
                ImGui.endTabBar();
            }

        }
        ImGui.end();

        if (ImGui.begin("System.in")){
            Thread thread = new Thread(()->{
                while (true){
                    Scanner scanner = new Scanner(System.in);
                    String nextLine = scanner.nextLine();
                    if (!nextLine.isEmpty()){
                        ImGuiServerHandler.this.nextLine += nextLine + "\n";
                    }
                }
            },"Scanner");
            thread.start();

            ImGui.text(this.nextLine);
        }
        ImGui.end();
    }

    private void drawPlayerOnMap(Player player, float drawWidth, float drawHeight) {
        Vector3 playerPos = player.getPosition();
        Quaternion playerRot = player.getRotation();
        TextureLoader loader = new TextureLoader();
        Texture tex = loader.getImage("player.png",getClass());

        // Spielwelt-Koordinaten-Bereich
        float worldMinX = -6500, worldMaxX = 6500;
        float worldMinZ = -6500, worldMaxZ = 6500;

// Bildgröße
        float imagePixelWidth = tex.width();
        float imagePixelHeight = tex.height();

// Mapping von Welt → Bild
        float playerMapX = ((playerPos.x - worldMinX) / (worldMaxX - worldMinX)) * drawWidth;
        float playerMapY = ((playerPos.z - worldMinZ) / (worldMaxZ - worldMinZ)) * drawHeight;

        float yaw = playerRot.yaw();

        ImDrawList drawList = ImGui.getWindowDrawList();

        float mapTopLeftX = ImGui.getItemRectMinX();
        float mapTopLeftY = ImGui.getItemRectMinY();

        float centerX = mapTopLeftX + playerMapX;
        float centerY = mapTopLeftY + playerMapY;
        float iconSize = 24f;

// Rotation vorbereiten
        float rad = (float) Math.toRadians(yaw);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

// Berechne 4 Ecken rotiert
        ImVec2[] points = new ImVec2[4];
        points[0] = new ImVec2(centerX + (-iconSize / 2) * cos - (-iconSize / 2) * sin,
                centerY + (-iconSize / 2) * sin + (-iconSize / 2) * cos); // Top-left
        points[1] = new ImVec2(centerX + (iconSize / 2) * cos - (-iconSize / 2) * sin,
                centerY + (iconSize / 2) * sin + (-iconSize / 2) * cos); // Top-right
        points[2] = new ImVec2(centerX + (iconSize / 2) * cos - (iconSize / 2) * sin,
                centerY + (iconSize / 2) * sin + (iconSize / 2) * cos); // Bottom-right
        points[3] = new ImVec2(centerX + (-iconSize / 2) * cos - (iconSize / 2) * sin,
                centerY + (-iconSize / 2) * sin + (iconSize / 2) * cos); // Bottom-left

// UV-Koordinaten (volle Textur)
        ImVec2 uv0 = new ImVec2(0, 0);
        ImVec2 uv1 = new ImVec2(1, 0);
        ImVec2 uv2 = new ImVec2(1, 1);
        ImVec2 uv3 = new ImVec2(0, 1);

// Zeichne mit addImageQuad
        drawList.addImageQuad(tex.id(), points[0], points[1], points[2], points[3],
                uv0, uv1, uv2, uv3, 0xFFFFFFFF);
    }


    private void setupDockspace() {
        ImGuiIO io = ImGui.getIO();
        int windowFlags = ImGuiWindowFlags.MenuBar
                | ImGuiWindowFlags.NoDocking
                | ImGuiWindowFlags.NoTitleBar
                | ImGuiWindowFlags.NoCollapse
                | ImGuiWindowFlags.NoResize
                | ImGuiWindowFlags.NoMove
                | ImGuiWindowFlags.NoBringToFrontOnFocus
                | ImGuiWindowFlags.NoNavFocus;
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPos());
        ImGui.setNextWindowSize(io.getDisplaySizeX(),io.getDisplaySizeY());
        ImGui.setNextWindowViewport(ImGui.getMainViewport().getID());


        ImGui.begin("DockSpace Root",windowFlags);

        if (ImGui.beginMenuBar()){
            if (ImGui.beginMenu("Window")){
                if (ImGui.menuItem("Preferences",showPreferences.get())){
                    showPreferences.set(!showPreferences.get());
                }
                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }




        int dockspaceId = ImGui.getID("MainDockSpace");
        ImGui.dockSpace(dockspaceId, 0,0, ImGuiDockNodeFlags.None);

        ImGui.end();
    }
    public void renderAnsiText(String text) {
        int pos = 0;
        while (pos < text.length()) {
            if (text.startsWith("\u001B[31m", pos)) {
                pos += 5; // ANSI-Code für Rot überspringen
                int end = text.indexOf("\u001B[0m", pos);
                if (end == -1) end = text.length();

                String redPart = text.substring(pos, end);
                ImGui.textColored(new ImVec4(1, 0, 0, 1), redPart);

                pos = end;
                if (pos < text.length() && text.startsWith("\u001B[0m", pos)) {
                    pos += 4; // Reset überspringen
                }
            }else if (text.startsWith("\u001B[32m", pos)){
                pos += 5; // ANSI-Code für Rot überspringen
                int end = text.indexOf("\u001B[0m", pos);
                if (end == -1) end = text.length();

                String redPart = text.substring(pos, end);
                ImGui.textColored(new ImVec4(0, 1, 0, 1), redPart);

                pos = end;
                if (pos < text.length() && text.startsWith("\u001B[0m", pos)) {
                    pos += 4; // Reset überspringen
                }
            } else if (text.startsWith("\u001B[", pos)) {
                // Unbekannter oder nicht unterstützter ANSI-Code → überspringen
                int end = text.indexOf('m', pos);
                if (end == -1) {
                    // Ungültiger ANSI-Code, beende Verarbeitung des restlichen Textes
                    break;
                }
                pos = end + 1;
            } else {
                int nextAnsi = text.indexOf("\u001B[", pos);
                if (nextAnsi == -1) nextAnsi = text.length();
                String normalPart = text.substring(pos, nextAnsi);
                ImGui.text(normalPart);
                pos = nextAnsi;
            }
        }
    }

    public void renderUserButton(Player player) {
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10, 10);

        float buttonHeight = 50;

        // Mache den Button über die ganze Breite, aber wir brauchen die echte Größe danach
        float availableWidth = ImGui.getContentRegionAvailX();

        if (ImGui.button("##userbutton", availableWidth, buttonHeight)) {
            ImGui.openPopup("UserActionsPopup");
        }

        // Berechne echte Position und Größe
        float x = ImGui.getItemRectMinX();
        float y = ImGui.getItemRectMinY();
        float width = ImGui.getItemRectSizeX(); // Jetzt echte Breite des Buttons

        // Zeichne den Namen
        ImGui.setCursorScreenPos(x + (width - ImGui.calcTextSize(player.getName()).x) / 2, y + 8);
        ImGui.text(player.getName());

        // Zeichne Herzen darunter
        String heartsText = "Health: (" + player.getHealth() + "/" + (player.getMaxHealth() * 4) + ")";
        ImGui.setCursorScreenPos(x + (width - ImGui.calcTextSize(heartsText).x) / 2, y + 28);
        ImGui.text(heartsText);

        ImGui.popStyleVar();

        // Kontextmenü
        if (ImGui.beginPopup("UserActionsPopup")) {
            if (ImGui.menuItem("Kicken")) {
                System.out.println("User " + player.getName() + " gekickt!");

                ImGui.closeCurrentPopup();
            }
            if (ImGui.menuItem("Bannen")) {
                System.out.println("User " + player.getName() + " gebannt!");

                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }
    }
}
