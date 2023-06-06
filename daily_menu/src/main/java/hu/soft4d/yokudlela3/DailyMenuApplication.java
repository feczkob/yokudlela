package hu.soft4d.yokudlela3;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@QuarkusMain
public class DailyMenuApplication {
    public static void main(String... args) {
        Quarkus.run(MyApp.class, args);
    }

    public static class MyApp implements QuarkusApplication {

        @Override
        public int run(String... args) {
            log.info("DailyMenuApplication has been started successfully.");
            Quarkus.waitForExit();
            return 0;
        }
    }
}

