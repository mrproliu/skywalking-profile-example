package github.mrproliu.skywalking.profile.example;

import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RequestMapping
@Controller
public class ProfileController {

    @RequestMapping("/profile/{name}")
    @ResponseBody
    public String profile() {
        process();
        return "Success";
    }

    @Trace(operationName = "service/processWithThreadPool")
    private void process() {
        final ExecutorService threadPool = Executors.newFixedThreadPool(2);
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        threadPool.submit(new Task1(countDownLatch));
        threadPool.submit(new Task2(countDownLatch));
        try {
            countDownLatch.await(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static final class Task1 implements Runnable {
        private final CountDownLatch countDownLatch;
        public Task1(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }
        @Override
        public void run() {
            countDownLatch.countDown();
        }
    }
    private static final class Task2 implements Runnable {
        private final CountDownLatch countDownLatch;
        public Task2(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }
        @Override
        public void run() {
//            countDownLatch.countDown();
        }
    }
}
