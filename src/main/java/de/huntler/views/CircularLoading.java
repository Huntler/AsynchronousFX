package de.huntler.views;

import de.huntler.AsyncTask;
import javafx.animation.RotateTransition;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.net.URL;

public class CircularLoading extends HBox {

    private Label text;
    private ImageView image;

    /**
     * The constructor loads the standard look and feel.
     */
    public CircularLoading() {
        // set up this
        this.setPrefHeight(32);
        this.setPrefWidth(300);
        this.setSpacing(8);

        // set up the image holder
        Pane imageHolder = new Pane();
        imageHolder.setPrefHeight(32);
        imageHolder.setPrefWidth(32);

        // set up the info label
        this.text = new Label("CircularLoading");
        this.text.setPrefHeight(32);

        // set up the image view
        this.image = new ImageView();
        URL resource = getClass().getResource("/CircularLoading/CircularLoading.png");

        this.image.setImage(new Image(resource.toExternalForm()));
        this.image.setTint("#ff0000");

        imageHolder.getChildren().add(this.image);

        this.getChildren().addAll(imageHolder, this.text);
    }

    /**
     * Method binds an {@link AsyncTask} with an {@link ImageView}
     * and rotates it while the task is running.
     */
    public void bind(AsyncTask task) {
        // set up the animation standard
        RotateTransition rotate = new RotateTransition(Duration.millis(500), this.image);
        rotate.setByAngle(360);
        rotate.setAutoReverse(true);

        rotate.setOnFinished(event -> {
            if (!task.isDone()) {
                rotate.play();
            }
        });

        rotate.play();
    }

    /**
     * Method sets the text of the label inside the view
     *
     * @param text should be the text to be shown
     */
    public void setText(String text) {
        this.image.setVisible(true);
        this.text.setText(text);

        if (text == null) this.image.setVisible(false);
    }

    /**
     * Method gets the text from the label
     *
     * @return returns the labels text
     */
    public String getText() {
        return this.text.getText();
    }

    /**
     * Method changes the image of the loading animation
     *
     * @param image should be the image to set
     */
    public void setImage(String image) {
        this.image.setImage(new Image(image));
    }

    /**
     * returns the current image of the loading animation
     *
     * @return -
     */
    public Image getImage() {
        return this.image.getImage();
    }

    /**
     * Method tints the loading image to the given hex-color
     *
     * @param tint should be a color in hex-format
     */
    public void setTint(String tint) {
        this.image.setTint(tint);
    }

    /**
     * returns the current tint of the loading image as hex-string
     *
     * @return returns the tint color as hex-string, can be null
     */
    public String getTint() {
        return this.image.getTint();
    }
}
