package ch.w3tec.qt.api.domain.service.mail;

import ch.w3tec.qt.api.persistence.entity.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailContentBuilder {
    private TemplateEngine templateEngine;

    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String build(Tournament tournament, String appPath) {
        Context context = new Context();
        context.setVariable("name", tournament.getName());
        context.setVariable("owner", tournament.getOwner());
        context.setVariable("inviteLink", appPath + "/" + tournament.getVisitorId());
        context.setVariable("adminLink", appPath + "/" + tournament.getAdminId() + "/admin");
        return templateEngine.process("mail.template", context);
    }
}
