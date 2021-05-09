Nomes: 

Andressa Harume Rodrigues Andon
Giovanna Matos dos Santos
Sara Reis Miranda

Fatec – Curso: Análise e Desenvolvimento de Sistemas
Disciplina: Teste de Software
Prof. Edson Saraiva de Almeida
Desenvolvimento Dirigido por Teste


/* CLASSE REQ01CADASTRARLIVROMTEST */

package com.fatecNoturno.scel;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; 
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner; 
import org.springframework.test.web.servlet.MockMvc; 
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders; 
import org.springframework.test.web.servlet.result.MockMvcResultMatchers; 
import org.springframework.test.web.servlet.result.ViewResultMatchers;
import com.fatecNoturno.scel.model.livro;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringJUnit4ClassRunner.class)
public class REQ01CadastrarLivroTest {

@Autowired

MockMvc mockMvc;

@Test
public void ct01_cadastrar_livro_com_sucesso_retorna_size() throws Exception {
//	isbn, autor e tiulo não estão cadastrtados e o usuario digita corretamente 

	mockMvc.perform(MockMvcRequestBuilders.post("/livros/save") // Requisicao post pelo usuario
	.param("ISBN", "0789").param("AUTOR", "Jorge Amado").param("TITULO", "Capitaes da Areia"))
	// Então - retorna erro
	.andExpect(MockMvcResultMatchers.status().is(200)) // 404
	.andExpect(MockMvcResultMatchers.view().name("cadastrarlivro"))

	.andExpect(MockMvcResultMatchers.model().attribute("livro", Matchers.any(livro.class)))

	.andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("livro", "autor"))

	.andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("livro", "autor", "Size"));
	}
}


@Test
public void ct02_erro_ao_cadastrar_livro_ja_retorna_size() throws Exception {
//	isbn, autor e tiulo já foram cadastrados e o usuario digita novamente isbn, autor e titulo
	mockMvc.perform(MockMvcRequestBuilders.post("/livros/save") // simula uma requisicao post entrada pelo usuario
	.param("ISBN", "1234").param("AUTOR", "Jorge Amado").param("TITULO", "Capitaes da Areia"))

	.andExpect(MockMvcResultMatchers.status().is(500)) // 404 Livro já cadastrado anteriormente
	.andExpect(MockMvcResultMatchers.view().name("cadastrarlivro"))

	.andExpect(MockMvcResultMatchers.model().attribute("livro", Matchers.any(livro.class)))

	.andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("livro", "autor"))

	.andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("livro", "autor", "Size"));
	}
}

@Test
public void ct03_quando_isbn_branco_retorna_size() throws Exception {
//	isbn nao esta cadastrando, o usuario deixa o isbn em branco
	mockMvc.perform(MockMvcRequestBuilders.post("/livros/save") // simula uma requisicao post entrada pelo usuario
	.param("ISBN", "").param("AUTOR", "Jorge Amado").param("TITULO", "Capitaes da Areia"))

	.andExpect(MockMvcResultMatchers.status().is(200)) // 404
	.andExpect(MockMvcResultMatchers.view().name("cadastrarlivro"))

	.andExpect(MockMvcResultMatchers.model().attribute("livro", Matchers.any(livro.class)))

	.andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("livro", "autor"))

	.andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("livro", "autor", "Size"));
	}
}


@Test
public void ct04_quando_titulo_branco_retorna_size() throws Exception {
//	livro nao esta cadastrando e o usuario deixa o titulo em branco
	mockMvc.perform(MockMvcRequestBuilders.post("/livros/save") // simula uma requisicao post entrada pelo usuario
	.param("ISBN", "1234").param("AUTOR", "Jorge Amado").param("TITULO", ""))

	.andExpect(MockMvcResultMatchers.status().is(200)) // 404
	.andExpect(MockMvcResultMatchers.view().name("cadastrarlivro"))

	.andExpect(MockMvcResultMatchers.model().attribute("livro", Matchers.any(livro.class)))

	.andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("livro", "autor"))

	.andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("livro", "autor", "Size"));
	}
}

@Test
public void ct05_quando_autor_branco_retorna_size() throws Exception {
//	o autor nao esta cadastrando e o usuario deixa o autor em branco
	mockMvc.perform(MockMvcRequestBuilders.post("/livros/save") // simula uma requisicao post entrada pelo usuario
	.param("ISBN", "1234").param("AUTOR", "").param("TITULO", "Capitaes da Areia"))
	// Então - retorna erro
	.andExpect(MockMvcResultMatchers.status().is(200)) // 404
	.andExpect(MockMvcResultMatchers.view().name("cadastrarlivro"))

	.andExpect(MockMvcResultMatchers.model().attribute("livro", Matchers.any(livro.class)))

	.andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("livro", "autor"))

	.andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("livro", "autor", "Size"));
	}
}


/* CONTROLLER */

package com.fatecNoturno.scel.controller;

import com.fatecNoturno.scel.model.livro;
import com.fatecNoturno.scel.servico.livroService;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; 
import org.springframework.validation.BindingResult; 
import org.springframework.web.bind.annotation.*; 
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder; 
import org.apache.logging.log4j.LogManager; 
import org.apache.logging.log4j.Logger;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Controller
@RequestMapping(path = "/livros")
public class LivroController {

@Autowired
private livroService service;

Logger logger = LogManager.getLogger(LivroController.class);

@RequestMapping("/cadastrar")
public ModelAndView retornaFormDeCadastroDe(livro livro) { ModelAndView mv = new ModelAndView("cadastrarlivro"); mv.addObject("livro", livro);
return mv;
}

@RequestMapping(value = "/consultar", method = RequestMethod.GET)
public ResponseEntity<List<livro>> findAll() {
List<livro> obj = service.findAll();
return ResponseEntity.ok().body(obj);
}

@RequestMapping(value = "/consultar/{id}", method = RequestMethod.GET)
public ResponseEntity<livro> find(@PathVariable Long id) {
livro obj = service.find(id);
return ResponseEntity.ok().body(obj);
}

@RequestMapping(value = "/inserir", method = RequestMethod.POST)
public ResponseEntity<Void> insert(@RequestBody livro obj) {
obj = service.insert(obj);
URI uri =
ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getI d()).toUri();

return ResponseEntity.created(uri).build();
}
 
@RequestMapping("/save")
public ModelAndView save(@Valid livro livro, BindingResult result) { ModelAndView modelAndView = new ModelAndView("cadastrarlivro"); try {

service.insert(livro);
modelAndView.addObject("livros", service .findAll()); modelAndView.addObject("message", "Livro cadastrado");

} catch (Exception e) {
logger.info("Dados inválidos, verifique e tente novamente");
modelAndView.addObject("message", "");

} else {
modelAndView.addObject("message", "Livro ja foi cadastrado anteriormente");
}
}
return modelAndView;
}

@RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody livro obj) { obj.setId(id);

obj = service.update(obj);
return ResponseEntity.noContent().build();
}

@RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
public ResponseEntity<Void> delete(@PathVariable Long id) {
service.delete(id);
return ResponseEntity.noContent().build();
}

}

/* SERVICE */


package com.fatecNoturno.scel.servico;
import com.fatecNoturno.scel.model.livro;
import com.fatecNoturno.scel.model.LivroRepository; 
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service; 
import java.util.List;
import java.util.Optional;

@Service
public class LIVROSERVICE {

@Autowired
livroRepository repo;

public List<livro> findAll() {
return (List<livro>) repo.findAll();
}

public livro find(Long id) {
Optional<livro> obj = repo.findById(id);
return obj.orElseThrow(

() -> new ObjectNotFoundException(id, "Objeto não encontrado: " + livro.class.getName()));
}

public livro insert(livro obj) {
obj.setId(null);
return repo.save(obj);
 
}

public livro update(livro obj) {
find(obj.getId());
return repo.save(obj);
}

public void delete(Long id) {
find(id);
repo.delete(find(id));
}
}



