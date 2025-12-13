package com.kiwisha.project.controller.web;

import com.kiwisha.project.dto.PedidoDTO;
import com.kiwisha.project.service.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MisPedidosWebController {

    private final PedidoService pedidoService;

    @GetMapping("/mis-pedidos")
    public String misPedidos(
            Principal principal,
            @PageableDefault(size = 20, sort = "creadoEn", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        String email = principal != null ? principal.getName() : null;
        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        log.debug("Mostrando mis pedidos para email={}", email);

        Page<PedidoDTO> pedidos = pedidoService.obtenerPedidosPorEmail(email, pageable);
        model.addAttribute("pageTitle", "Mis pedidos");
        model.addAttribute("pedidos", pedidos);

        return "public/mis-pedidos";
    }
}
