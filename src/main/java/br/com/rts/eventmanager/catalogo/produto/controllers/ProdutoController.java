package br.com.rts.eventmanager.catalogo.produto.controllers;

import br.com.rts.eventmanager.catalogo.ProdutoDTO;
import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
import br.com.rts.eventmanager.catalogo.produto.entities.Produto;
import br.com.rts.eventmanager.catalogo.produto.mappers.ProdutoMapper;
import br.com.rts.eventmanager.catalogo.produto.services.ProdutoService;
import br.com.rts.eventmanager.catalogo.subcategoria.services.SubCategoriaService;
import br.com.rts.eventmanager.utils.WebUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService service;
    private final ProdutoMapper mapper;

    private final CategoriaService categoriaService;
    private final SubCategoriaService subCategoriaService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTOS_LISTAR')")
    public String list(HttpSession session,
                       Model model) {

        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (tenantId == null) {
            return "redirect:/dashboard";
        }

        if (activeEvId != null) {
            model.addAttribute("produtos",
                    service.findAllByInstituicaoAndEvento(tenantId, activeEvId)
                            .stream()
                            .map(mapper::entityToDTO)
                            .toList());
        } else {
            model.addAttribute("produtos", java.util.Collections.emptyList());
        }

        model.addAttribute("pageTitle", "Produtos");
        return "produto/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTOS_CADASTRAR')")
    public String add(HttpSession session,
                      RedirectAttributes redirectAttributes,
                      Model model) {

        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (activeEvId == null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Nenhum evento ativo cadastrado! Apenas usuários Master/Admin podem criar eventos.");
            return "redirect:/produtos";
        }

        model.addAttribute("produto", new ProdutoDTO());
        model.addAttribute("categorias", categoriaService.findAllByInstituicao(tenantId));
        model.addAttribute("pageTitle", "Novo Produto");

        return "produto/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTOS_CADASTRAR')")
    public String add(HttpSession session,
                      @ModelAttribute("produto") @Valid final ProdutoDTO produtoNovo,
                      final BindingResult bindingResult,
                      final RedirectAttributes redirectAttributes,
                      Model model) {

        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (activeEvId == null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Nenhum evento ativo cadastrado!");
            return "redirect:/produtos";
        }

        if (bindingResult.hasErrors()) {
            //TODO Caso de erro na tela, a categoria selecionada deve ser informado novamente
            model.addAttribute("categorias", categoriaService.findAllByInstituicao(tenantId));
            model.addAttribute("subCategorias", subCategoriaService.findAllByInstituicaoAndCategoria(tenantId, produtoNovo.getCategoria().getId()));
            model.addAttribute("pageTitle", "Novo Produto");
            return "produto/add";
        }

        Produto produto = mapper.dtoToEntity(produtoNovo);
        service.create(produto, activeEvId, tenantId);

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Produto cadastrado com sucesso!");
        return "redirect:/produtos";
    }

    @GetMapping("/edit/{produtoId}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTOS_EDITAR')")
    public String edit(@PathVariable Long produtoId,
                       HttpSession session,
                       Model model) {

        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (tenantId == null || activeEvId == null) {
            return "redirect:/produtos";
        }

        Produto produto = service.findByIdAndInstituicaoAndEvento(produtoId, tenantId, activeEvId);

        model.addAttribute("produto", mapper.entityToDTO(produto));
        model.addAttribute("categorias", categoriaService.findAllByInstituicao(tenantId));
        model.addAttribute("subCategorias", subCategoriaService.findAllByInstituicaoAndCategoria(tenantId, produto.getCategoria().getId()));
        model.addAttribute("pageTitle", "Editar Produto");

        return "produto/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTOS_EDITAR')")
    public String edit(@PathVariable Long id,
                       HttpSession session,
                       @ModelAttribute("produto") @Valid final ProdutoDTO produto,
                       final BindingResult bindingResult,
                       final RedirectAttributes redirectAttributes,
                       Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (tenantId == null || activeEvId == null) {
            return "redirect:/produtos";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorias", categoriaService.findAllByInstituicao(tenantId));
            model.addAttribute("subCategorias", subCategoriaService.findAllByInstituicaoAndCategoria(tenantId, produto.getCategoria().getId()));
            model.addAttribute("pageTitle", "Editar Produto");
            return "produto/edit";
        }

        Produto produtoNew = mapper.dtoToEntity(produto);

        service.update(id, produtoNew, tenantId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Produto atualizado com sucesso!");
        return "redirect:/produtos";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTOS_DELETAR')")
    public String delete(@PathVariable Long id, HttpSession session, final RedirectAttributes redirectAttributes) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (tenantId != null && activeEvId != null) {
            try {
                service.delete(id, tenantId, activeEvId);
                redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, "Produto excluído com sucesso!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Não foi possível excluir o produto pois ele está referenciado em vendas ou estoques.");
            }
        }
        return "redirect:/produtos";
    }

    /**
     * Endpoint AJAX: retorna as subcategorias de uma categoria específica da instituição ativa.
     * Usado para popular o combo de subcategoria dinamicamente ao selecionar uma categoria.
     */
    @GetMapping("/subcategorias")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTOS_CADASTRAR', 'PRODUTOS_EDITAR')")
    public ResponseEntity<List<Map<String, Object>>> getSubCategoriasPorCategoria(
            @RequestParam Long categoriaId,
            HttpSession session) {

        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        if (tenantId == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Map<String, Object>> lista = subCategoriaService
                .findAllByInstituicaoAndCategoria(tenantId, categoriaId)
                .stream()
                .map(s -> Map.<String, Object>of("id", s.getId(), "nome", s.getNome()))
                .toList();
        return ResponseEntity.ok(lista);
    }

    /**
     * Endpoint AJAX: retorna os produtos de uma categoria e opcionalmente subcategoria específica da instituição ativa no evento ativo.
     */
    @GetMapping("/filtrar")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTOS_CADASTRAR', 'PRODUTOS_EDITAR')")
    public ResponseEntity<List<Map<String, Object>>> filtrarProdutos(
            @RequestParam Long categoriaId,
            @RequestParam(required = false) Long subCategoriaId,
            HttpSession session) {

        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");
        if (tenantId == null || activeEvId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Produto> produtos;
        if (subCategoriaId != null) {
            produtos = service.findAllByInstituicaoAndEventoAndCategoriaIdAndSubCategoriaId(tenantId, activeEvId, categoriaId, subCategoriaId);
        } else {
            produtos = service.findAllByInstituicaoAndEventoAndCategoriaId(tenantId, activeEvId, categoriaId);
        }

        List<Map<String, Object>> lista = produtos.stream()
                .map(p -> Map.<String, Object>of("id", p.getId(), "nome", p.getNome()))
                .toList();
        return ResponseEntity.ok(lista);
    }
}
