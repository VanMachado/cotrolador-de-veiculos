package com.van.log.api.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.van.log.api.dtos.requests.UsuarioRequestDto;
import com.van.log.api.dtos.requests.VeiculoRequestDto;
import com.van.log.domain.models.Usuario;
import com.van.log.domain.models.Veiculo;
import com.van.log.domain.repositories.UsuarioRepository;
import com.van.log.domain.repositories.VeiculoRepository;
import com.van.log.domain.services.VeiculoService;

@RestController
public class UsuarioController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private VeiculoRepository veiculoRepository;

	@Autowired
	private VeiculoService veiculoService;

	@PostMapping("/usuarios")
	public ResponseEntity<?> adicionar(@Valid @RequestBody UsuarioRequestDto usuarioRequestDto,
			UriComponentsBuilder uriComponentsBuilder) {

		Usuario usuario = usuarioRequestDto.toModel();

		Usuario salvo = usuarioRepository.save(usuario);

		return ResponseEntity.created(uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(salvo.getId()).toUri())
				.body(salvo);
	}

	@GetMapping("/usuarios/{idUsuario}/veiculos")
	public ResponseEntity<List<Veiculo>> listar(@PathVariable Integer idUsuario,
			UriComponentsBuilder uriComponentsBuilder) {

		Optional<Usuario> findById = usuarioRepository.findById(idUsuario);

		if (findById.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Usuario usuario = findById.get();

		List<Veiculo> veiculos = veiculoRepository.findByUsuario(usuario);

		return ResponseEntity
				.created(uriComponentsBuilder.path("/usuarios/{id}/veiculos").buildAndExpand(usuario.getId()).toUri())
				.body(veiculos);

	}

	@PostMapping("/usuarios/{idUsuario}/veiculos")
	public ResponseEntity<Veiculo> cadastrarVeiculo(@PathVariable Integer idUsuario,
			@Valid @RequestBody VeiculoRequestDto veiculoRequestDto, UriComponentsBuilder uriComponentsBuilder) {

		Optional<Usuario> findById = usuarioRepository.findById(idUsuario);

		if (findById.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Usuario usuario = findById.get();

		Veiculo veiculo = veiculoRequestDto.toModel();

		veiculo.setUsuario(usuario);
		
		Veiculo cadastrado = veiculoService.cadastrar(veiculo);

		return ResponseEntity.created(uriComponentsBuilder.path("/usuarios/{idUsuario}/veiculos/{idVeiculo}")
				.buildAndExpand(usuario.getId(), cadastrado.getId()).toUri()).body(cadastrado);

	}
}
