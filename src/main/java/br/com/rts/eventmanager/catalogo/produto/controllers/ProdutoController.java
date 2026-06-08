package br.com.rts.eventmanager.catalogo.produto.controllers;

import br.com.rts.eventmanager.catalogo.categoria.services.CategoriaService;
import br.com.rts.eventmanager.catalogo.produto.entities.Produto;
import br.com.rts.eventmanager.catalogo.produto.mappers.ProdutoMapper;
import br.com.rts.eventmanager.catalogo.produto.services.ProdutoService;
import br.com.rts.eventmanager.catalogo.subcategoria.services.SubCategoriaService;
import br.com.rts.eventmanager.catalogo.ProdutoDTO;
import br.com.rts.eventmanager.utils.WebUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Log4j2
@Controller
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService service;
    private final ProdutoMapper mapper;

    private final CategoriaService categoriaService;
    private final SubCategoriaService subCategoriaService;

    //    @ModelAttribute
//    public void prepareContext(final Model model) {
//        model.addAttribute("categoriaIdValues", categoriaMapper.entityToDTO(categoriaService.findAll()));
//    }

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
                            .map(mapper::entityToDTO));
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

        Produto produto = new Produto();
        model.addAttribute("produto", produto);
        model.addAttribute("categorias", categoriaService.findAllByInstituicao(tenantId));
        //TODO subcategoria somente deve ser carregado, quando selecionado uma categoria
//        model.addAttribute("subCategorias", subCategoriaService.findAllByInstituicao(tenantId, Pageable.ofSize(100)).getContent());
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
            model.addAttribute("categorias", categoriaService.findAllByInstituicao(tenantId, Pageable.ofSize(100)).getContent());
            //TODO subcategoria somente deve ser carregado, quando selecionado uma categoria
            model.addAttribute("subCategorias", subCategoriaService.findAllByInstituicao(tenantId, Pageable.ofSize(100)).getContent());
            model.addAttribute("pageTitle", "Novo Produto");
            return "produto/add";
        }

        Produto produto = mapper.dtoToEntity(produtoNovo);

        service.create(produto, tenantId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Produto cadastrado com sucesso!");
        return "redirect:/produtos";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTOS_EDITAR')")
    public String edit(@PathVariable Long id, HttpSession session, Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (tenantId == null || activeEvId == null) {
            return "redirect:/produtos";
        }

        Produto produto = service.findByIdAndInstituicaoAndEvento(id, tenantId, activeEvId);
        if (produto == null) {
            throw new IllegalArgumentException("Produto não encontrado!");
        }

        model.addAttribute("produto", produto);
        model.addAttribute("categorias", categoriaService.findAllByInstituicao(tenantId, Pageable.ofSize(100)).getContent());
        model.addAttribute("subCategorias", subCategoriaService.findAllByInstituicao(tenantId, Pageable.ofSize(100)).getContent());
        model.addAttribute("pageTitle", "Editar Produto");
        return "produto/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTOS_EDITAR')")
    public String edit(@PathVariable Long id,
                       HttpSession session,
                       @ModelAttribute("produto") @Valid final Produto produto,
                       final BindingResult bindingResult,
                       final RedirectAttributes redirectAttributes,
                       Model model) {
        Long tenantId = (Long) session.getAttribute("activeInstituicaoId");
        Long activeEvId = (Long) session.getAttribute("activeEventoId");

        if (tenantId == null || activeEvId == null) {
            return "redirect:/produtos";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorias", categoriaService.findAllByInstituicao(tenantId, Pageable.ofSize(100)).getContent());
            model.addAttribute("subCategorias", subCategoriaService.findAllByInstituicao(tenantId, Pageable.ofSize(100)).getContent());
            model.addAttribute("pageTitle", "Editar Produto");
            return "produto/edit";
        }

        Produto existing = service.findByIdAndInstituicaoAndEvento(id, tenantId, activeEvId);
        if (existing == null) {
            throw new IllegalArgumentException("Produto não encontrado!");
        }

        existing.setNome(produto.getNome());
        existing.setEspecificacao(produto.getEspecificacao());
        existing.setValorVendaUnitario(produto.getValorVendaUnitario());
        existing.setQuantidadeMinima(produto.getQuantidadeMinima());
        existing.setCategoria(produto.getCategoria());
        existing.setSubCategoria(produto.getSubCategoria());
        existing.setLastUpdated(java.time.LocalDateTime.now());

        service.update(id, existing, tenantId);
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

//    @GetMapping("/subcategorias-fragment")
//    @PreAuthorize("hasAnyAuthority('ROLE_MASTER', 'PRODUTO_LISTAGEM', 'PRODUTO_CADASTRAR', 'PRODUTO_EDITAR')")
//    public String getSubCategoriasFragment(@RequestParam(name = "categoriaId", required = false) final Long categoriaId,
//                                           final Model model) {
//
//        ProdutoDTO produto = new ProdutoDTO();
//        produto.setCategoriaId(categoriaId);
//
//        model.addAttribute("produto", produto);
//
//        if (categoriaId != null) {
//            model.addAttribute("subCategoriaIdValues", subCategoriaMapper.entityToDTO(subCategoriaService.findAllByCategoria(categoriaId)));
//        } else {
//            model.addAttribute("subCategoriaIdValues", emptyMap());
//        }
//
//        return "produto/fragments/subCategoriaOptions :: select";
//    }
// }
}
