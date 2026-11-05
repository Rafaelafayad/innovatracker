package hiragi.innovatracker.service;


import hiragi.innovatracker.dto.LoginRequestDTO;
import hiragi.innovatracker.dto.LoginResponseDTO;
import hiragi.innovatracker.dto.PessoaResumoDTO;
import hiragi.innovatracker.model.TbPessoa;
import hiragi.innovatracker.repository.TbPessoaRepository;
import hiragi.innovatracker.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class AuthService {

    private static final String CREDENCIAIS_INVALIDAS = "E-mail ou senha inválidos";

    private final TbPessoaRepository pessoaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final long jwtExpirationMs;


    public AuthService(TbPessoaRepository pessoaRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       @Value("${app.jwt.expiration-ms}") long jwtExpirationMs) {
        this.pessoaRepository = pessoaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtExpirationMs = jwtExpirationMs;
    }


    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {


        String email = request.email().trim();


        TbPessoa pessoa = pessoaRepository.findByEmlPessoaIgnoreCase(email)
                .orElseThrow(() -> {
                    log.warn("Tentativa de login com e-mail inexistente: {}", email);
                    return new BadCredentialsException(CREDENCIAIS_INVALIDAS);
                });


        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


        String senhaPura = "654321";
        String hashGerado = encoder.encode(senhaPura);


        System.out.println("==================================================");
        System.out.println("Senha em texto puro : " + senhaPura);
        System.out.println("Hash gerado pelo Java: " + hashGerado);
        System.out.println("==================================================");


        if (!passwordEncoder.matches(request.senha(), pessoa.getPwdPessoa())) {
            log.warn("Senha incorreta para o e-mail: {}", email);
            throw new BadCredentialsException(CREDENCIAIS_INVALIDAS);
        }


        if (Boolean.FALSE.equals(pessoa.getFlgAtivoPessoa())) {
            throw new DisabledException("Usuário inativo. Contate o administrador.");
        }


        // subject do token = e-mail (é o que o filtro lê com getUsernameFromToken)
        String token = jwtTokenProvider.generateToken(pessoa.getEmlPessoa());


        log.info("Login realizado com sucesso: idtPessoa={}", pessoa.getIdtPessoa());


        return LoginResponseDTO.of(token, jwtExpirationMs, PessoaResumoDTO.from(pessoa));
    }


    /** Usado pelo endpoint /api/auth/me, com o e-mail vindo do subject do token. */
    @Transactional(readOnly = true)
    public PessoaResumoDTO buscarPorEmail(String email) {
        return pessoaRepository.findByEmlPessoaIgnoreCase(email)
                .map(PessoaResumoDTO::from)
                .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado"));
    }
}
