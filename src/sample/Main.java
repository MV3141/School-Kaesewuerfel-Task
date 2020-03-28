package sample;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.util.Arrays;

public class Main extends Application {

    final double WIDTH = 600;
    final double HEIGHT = 600;

    double cubeWidth = 30;

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);

    private final int[][][] cuboid = new int[17][20][24];
    private final int[][][] cuboidProcessed = new int[cuboid.length][cuboid[1].length][cuboid[0][1].length];

    private int curSegment = 0;

    @Override
    public void start(Stage primaryStage) {

        // creating a Group
        Group root = new Group();

        cubeWidth = WIDTH/Math.max(Math.max(cuboid.length, cuboid[1].length), cuboid[0][1].length)*0.6;

        PhongMaterial material = null;

        for (int x = 0; x < cuboid.length; x++) {
            for (int y = 0; y < cuboid[1].length; y++) {
                for (int z = 0; z < cuboid[0][1].length; z++) {
                    cuboid[x][y][z] = Math.random() < 0.50 ? 1 : 0;
                   /* if (cuboid[x][y][z] > 0) {
                        material = new PhongMaterial(Color.YELLOW);
                        Box cube = createCube(material);
                        cube.setTranslateX(x * cubeWidth - (cuboid.length - 1) * cubeWidth * 0.5); // = x * cubeWidth - cuboid.length * cubeWidth * 0.5 + cubeWidth * 0.5
                        cube.setTranslateY(y * cubeWidth - (cuboid[1].length - 1) * cubeWidth * 0.5);
                        cube.setTranslateZ(z * cubeWidth - (cuboid[0][1].length - 1) * cubeWidth * 0.5);
                        root.getChildren().add(cube);
                    }*/
                }
            }
        }

        System.out.println(Arrays.deepToString(cuboid[0]));
        System.out.println(Arrays.deepToString(cuboid[1]));
        System.out.println(Arrays.deepToString(cuboid[2]));

        for (int x = 0; x < cuboid.length; x++) {
            for (int y = 0; y < cuboid[1].length; y++) {
                for (int z = 0; z < cuboid[0][1].length; z++) {
                    if (cuboid[x][y][z] != 0) {
                        curSegment++;
                        connectionCheck(x, y, z);
                    }
                }
            }
        }

        for (int x = 0; x < cuboidProcessed.length; x++) {
            for (int y = 0; y < cuboidProcessed[1].length; y++) {
                for (int z = 0; z < cuboidProcessed[0][1].length; z++) {
                    if (cuboidProcessed[x][y][z] > 0) {
                        //System.err.println("Yes");
                        material = new PhongMaterial();
                        material.setDiffuseColor(Color.web("hsla(" + (int)(255*(curSegment/255+1) / curSegment) * cuboidProcessed[x][y][z] + ",100%,100%,1.0)"));
                        Box cube = createCube(material);
                        cube.setTranslateX(x * cubeWidth - (cuboidProcessed.length - 1) * cubeWidth * 0.5); // = x * cubeWidth - cuboid.length * cubeWidth * 0.5 + cubeWidth * 0.5
                        cube.setTranslateY(y * cubeWidth - (cuboidProcessed[1].length - 1) * cubeWidth * 0.5);
                        cube.setTranslateZ(z * cubeWidth - (cuboidProcessed[0][1].length - 1) * cubeWidth * 0.5);
                        root.getChildren().add(cube);
                    }
                }
            }
        }
        System.out.println("***********************************************************************************************************************************************************************");
        System.out.println("This is " + (curSegment == 1 ? "": "not") + " a 'Kaesewuerfel'.");
        System.out.println("It consists of " + curSegment + " segment(s).");

        /*for (int i = 0; i < cuboid.length; i++) {
            System.out.println(Arrays.deepToString(cuboid[i]));
        }*/

        //System.out.println(Arrays.deepToString(cuboid));

        root.translateXProperty().set(WIDTH * 0.5);
        root.translateYProperty().set(HEIGHT * 0.5);
        root.translateZProperty().set(0);

        Scene scene = new Scene(root, WIDTH, HEIGHT, true);

        // creating Camera
        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.setTranslateX(0);
        camera.setTranslateY(0);
        camera.setTranslateZ(0);

        scene.setCamera(camera);
        primaryStage.setTitle("Kaesewuerfel");
        primaryStage.setScene(scene);
        primaryStage.show();

        initMouseControl(root, scene);

    }

    private void connectionCheck(int x, int y, int z) {
        if (cuboid[x][y][z] != 0) {
            // set to zero so it can't be checked twice
            cuboid[x][y][z] = 0;
            cuboidProcessed[x][y][z] = curSegment;
            if (z < cuboid[0][1].length - 1)
                connectionCheck(x, y, z + 1);
            if (y < cuboid[1].length - 1)
                connectionCheck(x, y + 1, z);
            if (x < cuboid.length - 1)
                connectionCheck(x + 1, y, z);
            if (z > 0)
                connectionCheck(x, y, z - 1);
            if (y > 0)
                connectionCheck(x, y - 1, z);
            if (x > 0)
                connectionCheck(x - 1, y, z);
        }
    }

    private void initMouseControl(Group group, Scene scene) {
        Rotate xRotate;
        Rotate yRotate;
        group.getTransforms().addAll(
                xRotate = new Rotate(25, Rotate.X_AXIS),
                yRotate = new Rotate(25, Rotate.Y_AXIS)
        );
        xRotate.angleProperty().bind(angleX);
        yRotate.angleProperty().bind(angleY);

        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                anchorX = event.getSceneX();
                anchorY = event.getSceneY();
                anchorAngleX = angleX.get();
                anchorAngleY = angleY.get();
            }
        });

        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                angleX.set(anchorAngleX - (anchorY - event.getSceneY()));
                angleY.set(anchorAngleY + anchorX - event.getSceneX());
            }
        });

    }

    private Box createCube(PhongMaterial material) {

        PhongMaterial material1 = new PhongMaterial();
        material1.setSpecularColor(Color.BLACK);
        material1.setDiffuseColor(Color.RED);

        // creating a cube (box)
        Box box = new Box();
        // properties of the box
        box.setWidth(cubeWidth);
        box.setHeight(cubeWidth);
        box.setDepth(cubeWidth);

        box.setMaterial(material);


        return box;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
