package manager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {


    @Override
    public InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }


}