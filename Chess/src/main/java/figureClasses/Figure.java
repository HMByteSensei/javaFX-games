package figureClasses;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Figure extends ImageView {
    private static ArrayList<VBox> listOfPlaceholders;
    private static ArrayList<VBox> listOfEatPlaceholders;
    private VBox figureToMove = null;
    private static GridPane tabla;
    private HashMap<VBox, EventHandler<? super MouseEvent>> originalClickHandler = new HashMap<>();

    public Figure(String path, VBox cell) {
        cell.getChildren().add(new ImageView(path));
        cell.setOnMouseClicked(event -> canMoveOnClick(event));
        listOfPlaceholders = new ArrayList<>();
        listOfEatPlaceholders = new ArrayList<>();
    }
    // before program starts set tabla
    public static void setTabla(GridPane t) { tabla = t; }

    public void drawPlaceholder(VBox cell) {
        Circle placeholder = new Circle(10);
        placeholder.setFill(Color.GRAY);
        placeholder.setOnMouseClicked(event -> moveOnClick(event));
        cell.getChildren().add(placeholder);
        listOfPlaceholders.add(cell);
    }
    public void drawEatPlaceholder(VBox cell) {
        saveOriginalClickHandler(cell);
        cell.getStyleClass().add("eatPozadina");
        cell.setOnMouseClicked(ev -> eatOnClick(cell));
        listOfEatPlaceholders.add(cell);
    }

    private void saveOriginalClickHandler(VBox cell) {
        originalClickHandler.put(cell, cell.getOnMouseClicked());
    }
    public void removeEatPlaceholder() {
        for(VBox cell : listOfEatPlaceholders) {
            cell.getStyleClass().removeAll("eatPozadina");
            cell.setOnMouseClicked(originalClickHandler.get(cell));
        }
        originalClickHandler.clear();
        listOfEatPlaceholders.clear();
    }

    public void removePlaceholder() {
        for (VBox cell : listOfPlaceholders) {
            cell.getChildren().removeIf(node -> node instanceof Circle);
        }
        listOfPlaceholders.clear();
        removeEatPlaceholder();
    }
    public boolean isFree(VBox cell) {
        // cell is VBox if there is no Figure at that cell it should be empty
        return cell.getChildren().isEmpty();
    }
    // to know from what cell we are moving
    public void setFigureToMove(VBox cell) {figureToMove = cell;}
    public void moveOnClick(MouseEvent event) {
        Node node = (Node) event.getSource();
        int rowIndex = GridPane.getRowIndex(node.getParent());
        int columnIndex = GridPane.getColumnIndex((node.getParent()));
        VBox moveToCell = (VBox) tabla.getChildren().get(rowIndex * 8 + columnIndex);
        ImageView figura = (ImageView) figureToMove.getChildren().get(0);
        figureToMove.getChildren().remove(figura);
        figureToMove.setOnMouseClicked(null);
        removePlaceholder();
        moveToCell.getChildren().add(figura);
        figureToMove = moveToCell;
        moveToCell.setOnMouseClicked(ev -> canMoveOnClick(ev));
    }
    public void eatOnClick(VBox cell) {
        cell.getChildren().remove(0);
        figureToMove.setOnMouseClicked(null);
        removePlaceholder();
        cell.getChildren().add((ImageView) figureToMove.getChildren().get(0));
        figureToMove.getChildren().removeAll();
        figureToMove = cell;
        cell.setOnMouseClicked(ev -> canMoveOnClick(ev));
    }
    public abstract void canMoveOnClick(MouseEvent event);
    public abstract void canEat(VBox cell);
    // to force subclasses for this getter
    public abstract String getColor();
}
