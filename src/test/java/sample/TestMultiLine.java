package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import sample.service.AppService;
import sample.view.emotion.EmotionViewModel;
import sample.view.tools.Dot;
import sample.view.tools.MultiLine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestMultiLine  extends Application {
    public static void main() throws Exception {
        AppService.getInstasnce().init();
        EmotionViewModel viewModel = new EmotionViewModel();
        viewModel.refreshLianBan();
        Map<String, List<Dot>> map = new HashMap<>();
//        map.put("连板高度", viewModel.lianbanIndexList);
//        map.put("跌停数量", viewModel.dieTingCountList);
//        map.put("涨停数量", viewModel.zhangTingCountList);
        MultiLine.drawKLine( viewModel.pointMap);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        main();
    }
}
