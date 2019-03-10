package okhttp3;

class EventListener$2 implements EventListener$Factory {
    final /* synthetic */ EventListener val$listener;

    EventListener$2(EventListener eventListener) {
        this.val$listener = eventListener;
    }

    public EventListener create(Call call) {
        return this.val$listener;
    }
}
