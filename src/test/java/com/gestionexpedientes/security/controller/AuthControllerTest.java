package com.gestionexpedientes.security.controller;

import com.gestionexpedientes.counter.service.CounterService;
import com.gestionexpedientes.global.exceptions.GlobalException;
import com.gestionexpedientes.security.enums.RoleEnum;
import com.gestionexpedientes.security.jwt.JwtProvider;
import com.gestionexpedientes.security.service.UserDetailsServiceImpl;
import com.gestionexpedientes.security.service.UserEntityService;
import com.gestionexpedientes.user.entity.UserEntity;
import com.gestionexpedientes.user.repository.IUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({UserEntityService.class, GlobalException.class})
class AuthControllerTest {

    private static final String ALTA_CON_ROLES = "{"
            + "\"name\":\"Ana\",\"lastname\":\"Vecina\",\"dni\":\"30111222\","
            + "\"address\":\"Calle Falsa 123\",\"email\":\"ana@demo.test\",\"password\":\"Abc123!x\","
            + "\"roles\":[\"ROLE_ADMIN\"]}";

    @Autowired private MockMvc mockMvc;

    @MockitoBean private IUserRepository userRepository;
    @MockitoBean private PasswordEncoder passwordEncoder;
    @MockitoBean private JwtProvider jwtProvider;
    @MockitoBean private AuthenticationManager authenticationManager;
    @MockitoBean private CounterService counterService;
    @MockitoBean private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("El registro publico crea siempre ROLE_USER aunque el body mande roles")
    void elRegistroPublicoIgnoraLosRolesDelBody() throws Exception {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByDni(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(counterService.nextId("users")).thenReturn(5);
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/auth/create-user").contentType(MediaType.APPLICATION_JSON).content(ALTA_CON_ROLES))
                .andExpect(status().isOk());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRoles()).containsExactly(RoleEnum.ROLE_USER);
    }

    @Test
    @DisplayName("El registro rechaza un email ya usado")
    void rechazaEmailDuplicado() throws Exception {
        when(userRepository.existsByEmail("ana@demo.test")).thenReturn(true);

        mockMvc.perform(post("/auth/create-user").contentType(MediaType.APPLICATION_JSON).content(ALTA_CON_ROLES))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El correo está en uso."));

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("El registro rechaza un DNI ya usado")
    void rechazaDniDuplicado() throws Exception {
        when(userRepository.existsByEmail("ana@demo.test")).thenReturn(false);
        when(userRepository.existsByDni("30111222")).thenReturn(true);

        mockMvc.perform(post("/auth/create-user").contentType(MediaType.APPLICATION_JSON).content(ALTA_CON_ROLES))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El DNI está en uso."));

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("El registro valida los campos obligatorios")
    void rechazaBodyIncompleto() throws Exception {
        mockMvc.perform(post("/auth/create-user").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Ana\"}"))
                .andExpect(status().isBadRequest());
    }
}
