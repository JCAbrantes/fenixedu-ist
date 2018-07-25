package pt.ist.registration.process.ui.service;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.BennuSpringContextHelper;
import org.fenixedu.bennu.core.domain.Bennu;
import org.springframework.stereotype.Service;

import pt.ist.registration.process.domain.DeclarationTemplate;
import pt.ist.registration.process.domain.RegistrationDeclarationFile;
import pt.ist.registration.process.handler.CandidacySignalHandler;

@Service
public class RegistrationProcessDeclarationsService {

    private Comparator<RegistrationDeclarationFile> COMPARATOR_BY_CREATION_DATE = new Comparator<RegistrationDeclarationFile>() {
        @Override
        public int compare(final RegistrationDeclarationFile f1, final RegistrationDeclarationFile f2) {
            return f2.getCreationDate().compareTo(f1.getCreationDate());
        }
    };

    public void generateDeclaration(Registration registration, ExecutionYear executionYear, DeclarationTemplate template) {
        CandidacySignalHandler bean = BennuSpringContextHelper.getBean(CandidacySignalHandler.class);
        bean.sendDocumentToBeSigned(registration, ExecutionYear.readCurrentExecutionYear(), template);
    }

    public void sendDocumentToBeSigned(Registration registration, RegistrationDeclarationFile file) {
        CandidacySignalHandler bean = BennuSpringContextHelper.getBean(CandidacySignalHandler.class);
        bean.sendDocumentToBeSigned(registration, ExecutionYear.readCurrentExecutionYear(), file);
    }

    public Set<DeclarationTemplate> getDeclarationTemplates() {
        return Bennu.getInstance().getDeclarationTemplateSet();
    }

    public Set<DeclarationTemplate> getSub23DeclarationTemplates() {
        return getDeclarationTemplates().stream().filter(dt -> dt.getName().contains("sub23")).collect(Collectors.toSet());
    }

    public Set<RegistrationDeclarationFile> getRegistrationDeclarationFileOrderedByDate(Registration registration) {
        Set<RegistrationDeclarationFile> file_by_date = new TreeSet<RegistrationDeclarationFile>(COMPARATOR_BY_CREATION_DATE);

        for (RegistrationDeclarationFile registrationDeclarationFile : registration.getRegistrationDeclarationFileSet())
            file_by_date.add(registrationDeclarationFile);

        return file_by_date;
    }
}
