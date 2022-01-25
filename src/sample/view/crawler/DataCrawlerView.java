package sample.view.crawler;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class DataCrawlerView  implements FxmlView<DataCrawlerViewModel>, Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lastCrawlerDate.textProperty().bind(viewModel.getLastCrawlerStockDate());
        lastCrawlerMarketDate.textProperty().bind(viewModel.getLastCrawlerMarketDate());
        crawlerDataProgressBar.progressProperty().bind(viewModel.getCrawlerStockProgress());
        crawlerMarketDataProgressBar.progressProperty().bind(viewModel.getCrawlerMarketProgress());
        crawlerDayIntervalField.textProperty().bindBidirectional(viewModel.getCrawlerDayInterval());
    }

    @InjectViewModel
    private DataCrawlerViewModel viewModel;

    @FXML
    private ProgressBar crawlerDataProgressBar;

    @FXML
    private Label lastCrawlerMarketDate;

    @FXML
    private Label lastCrawlerDate;

    @FXML
    private ProgressBar crawlerMarketDataProgressBar;

    @FXML
    private TextField crawlerDayIntervalField;

    @FXML
    void onStartCrawlerData(ActionEvent event) {
        viewModel.onStartCrawlerStockListData();
    }

    @FXML
    void onStartCrawlerMarketData(ActionEvent event) {
        viewModel.onStartCrawlerMarketData();
    }
}
