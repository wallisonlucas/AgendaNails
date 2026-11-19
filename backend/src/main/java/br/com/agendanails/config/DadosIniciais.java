package br.com.agendanails.config;

import br.com.agendanails.model.Usuario;
import br.com.agendanails.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DadosIniciais implements CommandLineRunner {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final String adminEmail;
    private final String adminSenha;

    public DadosIniciais(UsuarioRepository repository,
                         PasswordEncoder encoder,
                         @Value("${app.admin.email}") String adminEmail,
                         @Value("${app.admin.senha}") String adminSenha) {
        this.repository = repository;
        this.encoder = encoder;
        this.adminEmail = adminEmail;
        this.adminSenha = adminSenha;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setEmail(adminEmail);
            admin.setSenha(encoder.encode(adminSenha));
            admin.setPapel("ADMIN");
            repository.save(admin);
        }
    }
}