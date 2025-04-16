package manager;

import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {


    @Override
    public InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }


}