package sample.view.emotion;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import sample.view.tools.Dot;
import sample.view.tools.MultiLine;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class EmotionView   implements FxmlView<EmotionViewModel> , Initializable {

    @InjectViewModel
    private EmotionViewModel viewModel;

    @FXML
    private BorderPane klinePane;

    public void refresh() throws Exception {
        this.viewModel.refreshLianBan();
        MultiLine.drawKLine(klinePane, viewModel.pointMap);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Map<String, List<Dot>>  map = new HashMap<>();
        MultiLine.drawKLine(klinePane, map);
    }
}
