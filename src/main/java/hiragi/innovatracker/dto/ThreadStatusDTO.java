package hiragi.innovatracker.dto;


/**
 * Record em Java 25 para transporte de métricas da thread executora no InnovaTracker.
 */
public record ThreadStatusDTO(
        String threadName,
        boolean isVirtualThread,
        String threadGroup,
        long threadId
) {
    public static ThreadStatusDTO captureCurrent() {
        Thread thread = Thread.currentThread();
        return new ThreadStatusDTO(
                thread.getName(),
                thread.isVirtual(),
                thread.getThreadGroup() != null ? thread.getThreadGroup().getName() : "Sem Grupo",
                thread.threadId()
        );
    }
}
