package sample.view.tools;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import sample.model.StockInfo;

public class ButtonCell extends TableCell<StockInfo, Boolean> {
    final Button addButton      ;
    final StackPane paddedButton = new StackPane();
    final DoubleProperty buttonY = new SimpleDoubleProperty();

    final TableCellClick tableCellClick;
    /**
     * AddPersonCell constructor
     * @param table the table to which a new person can be added.
     */
    public ButtonCell(String buttonName, final TableView table, TableCellClick tableCellClick) {
        this.addButton = new Button(buttonName);
        this.tableCellClick = tableCellClick;
        paddedButton.setPadding(new Insets(3));
        paddedButton.getChildren().add(addButton);
        addButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                buttonY.set(mouseEvent.getScreenY());
            }
        });
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                table.getSelectionModel().select(getTableRow().getIndex());
                StockInfo selectStock = (StockInfo) getTableRow().getItem();
                tableCellClick.onTableCellClick(selectStock);
            }
        });
    }

    /** places an add button in the row only if the row is not empty. */
    @Override protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(paddedButton);
        } else {
            setGraphic(null);
        }
    }

}
