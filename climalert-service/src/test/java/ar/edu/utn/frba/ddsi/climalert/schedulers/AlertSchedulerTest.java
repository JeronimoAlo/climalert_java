package ar.edu.utn.frba.ddsi.climalert.schedulers;

import ar.edu.utn.frba.ddsi.climalert.services.AlertService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AlertSchedulerTest {

    @Mock
    private AlertService alertService;

    @Test
    void delegaEnAlertService() {
        AlertScheduler scheduler = new AlertScheduler(alertService);

        scheduler.ejecutar();

        verify(alertService).evaluarUltimoRegistro();
    }
}
