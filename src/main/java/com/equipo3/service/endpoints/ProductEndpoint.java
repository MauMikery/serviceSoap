package com.equipo3.service.endpoints;

import com.equipo3.service.clases.*;
import com.equipo3.service.model.Category;
import com.equipo3.service.model.CategoryRepository;
import com.equipo3.service.model.ProductDB;
import com.equipo3.service.model.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class ProductEndpoint {
	private static final String NAMESPACE_URI = "http://equipo3.com/clases";

	private ProductRepository productRepository;
	private CategoryRepository categoryRepository;

	@Autowired
	public ProductEndpoint(ProductRepository productRepository, CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}


	/*
	* ENDPOINTS
	* showProduct
	* addProduct
	* createProduct
	* readProducts
	* updateProduct
	* deleteProduct
	* */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "showProductRequest")
	@ResponsePayload
	public ShowProductResponse showProduct(@RequestPayload ShowProductRequest request) {
		ShowProductResponse response = new ShowProductResponse();

		//Buscamos el producto por id en la base de datos
		ProductDB productDB = productRepository.findById(request.getId()).orElse(null);

		if(productDB == null) {
			response.setStatus("El producto no ha sido encontrado");
			return response;
		} else {
			//Creamos un nuevo objeto producto para mandarlo como respuesta
			Product product = new Product();

			product.setId(productDB.getId());
			product.setName(productDB.getName());
			product.setPrice(productDB.getPrice());
			product.setDescription(productDB.getDescription());

			//Mandamos el objeto producto
			response.setStatus("Producto encontrado");
			response.setProduct(product);

			return response;
		}
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "addProductRequest")
	@ResponsePayload
	public AddProductResponse addProduct(@RequestPayload AddProductRequest request) {
		AddProductResponse response = new AddProductResponse();

		//Obtenemos los datos de la peticion
		String name = request.getName();
		String description = request.getDescription();
		Double price = request.getPrice();

		//Creamos un nuevo objeto de la base de datos
		ProductDB productDB = new ProductDB();
		productDB.setName(name);
		productDB.setDescription(description);
		productDB.setPrice(price);

		//Guardamos el producto en la base de datos
		productRepository.save(productDB);

		response.setStatus("Producto guardado correctamente");

		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "createProductRequest")
	@ResponsePayload
	public CreateProductResponse createProduct(@RequestPayload CreateProductRequest request) {
		CreateProductResponse response = new CreateProductResponse();

		//Obtenemos los datos de la peticion
		String name = request.getName();
		String description = request.getDescription();
		Double price = request.getPrice();
		String category = request.getCategory();

		//Verificamos que la categoria no existe
		if(categoryRepository.existsByName(category)){
			response.setStatus("El producto ya existe");
			return response;
		}

		//Creamos un objeto category y lo vamos a guardar
		Category categoryDB = new Category();
		categoryDB.setName(category);
		categoryRepository.save(categoryDB);

		//Creamos un nuevo objeto de la base de datos y lo vamos a guardar
		ProductDB productDB = new ProductDB();
		productDB.setName(name);
		productDB.setDescription(description);
		productDB.setPrice(price);
		productDB.setCategory(categoryRepository.findByName(category));

		//Guardamos el producto en la base de datos
		productRepository.save(productDB);

		response.setStatus("Producto guardado correctamente");

		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "readProductsRequest")
	@ResponsePayload
	public ReadProductsResponse readProducts(@RequestPayload ReadProductsRequest request) {
		
	}
}
