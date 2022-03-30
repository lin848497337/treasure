package sample.view.tools;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import sample.model.Algorithm;
import sample.view.strategy.CellClickHandler;

public class XCell extends ListCell<Algorithm> {
        HBox hbox = new HBox();
        Label label = new Label("(empty)");
        Pane pane = new Pane();
        Button button = new Button("(x)");
        Algorithm lastItem;

        public CellClickHandler buttonHandler;

        public XCell() {
            super();
            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (buttonHandler != null) {
                        buttonHandler.onClick(lastItem);
                    }
                }
            });
        }

        @Override
        protected void updateItem(Algorithm item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);  // No text in label of super class
            if (empty) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = item;
                label.setText(item!=null ? item.getName() : "<null>");
                setGraphic(hbox);
            }
        }
    }
