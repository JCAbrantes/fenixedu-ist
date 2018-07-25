package pt.ist.registration.process.domain;

import org.fenixedu.academic.domain.student.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.registration.process.ui.RegistrationProcessDeclarationController;
import pt.ist.registration.process.ui.service.SignCertAndStoreService;
import pt.tecnico.ulisboa.dsi.employer.exception.JobFailedException;
import pt.tecnico.ulisboa.dsi.employer.job.Job;

public class SignerJob extends Job {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationProcessDeclarationController.class);

    private SignCertAndStoreService signCertAndStoreService;

    private Registration registration;

    private RegistrationDeclarationFile file;

    private String fileQueue;

    public SignerJob(SignCertAndStoreService signCertAndStoreService, Registration registration,
            RegistrationDeclarationFile file, String fileQueue) {
        super();
        this.signCertAndStoreService = signCertAndStoreService;
        this.registration = registration;
        this.file = file;
        this.fileQueue = fileQueue;
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub

    }

    @Override
    @Atomic(mode = TxMode.READ)
    public void execute() throws JobFailedException {
        try {
            String filename = file.getFilename();
            String title = file.getDisplayName();
            String externalIdentifier = file.getUniqueIdentifier();

            signCertAndStoreService.sendDocumentToBeSigned(registration.getExternalId(), fileQueue, title, title, filename,
                    file.getStream(), externalIdentifier);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new JobFailedException();
        }
    }

    @Override
    @Atomic(mode = TxMode.READ)
    public void finish() {
        try {
            logger.debug("Registration Declaration {} of student {} was sent to signer", file.getUniqueIdentifier(),
                    registration.getNumber());
            file.updateState(RegistrationDeclarationFileState.PENDING);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    @Atomic(mode = TxMode.READ)
    public void fail() {
        logger.debug("Registration Declaration {} of student {} SignerJob failed", file.getUniqueIdentifier(),
                registration.getNumber());
    }

}
