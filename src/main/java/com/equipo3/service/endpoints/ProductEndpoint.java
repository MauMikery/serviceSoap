package com.equipo3.service.endpoints;

import com.equipo3.service.clases.*;
import com.equipo3.service.libs.ValidatorData;
import com.equipo3.service.model.Category;
import com.equipo3.service.model.CategoryRepository;
import com.equipo3.service.model.ProductDB;
import com.equipo3.service.model.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.ArrayList;
import java.util.List;

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

		//Si la categoria no existe la creamos
		if(!categoryRepository.existsByName(category)){
			//Creamos un objeto category y lo vamos a guardar en la BD
			Category categoryDB = new Category();
			categoryDB.setName(category);
			categoryRepository.save(categoryDB);
		}

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
		ReadProductsResponse response = new ReadProductsResponse();

		Iterable<ProductDB> productsDB = productRepository.findAll();
		List<Product> products = new ArrayList<>();

		productsDB.forEach(productDB -> {
			Product product = new Product();
			product.setId(productDB.getId());
			product.setName(productDB.getName());
			product.setDescription(productDB.getDescription());
			product.setPrice(productDB.getPrice());

			//Obtenemos la categoría
			Category category = productDB.getCategory();
			if(category != null){
				product.setCategory(category.getName());
			}

			products.add(product);
		});

		if(products.isEmpty()) {
			response.setStatus("No hay productos aún");

			return response;
		}

		response.setStatus("Productos leídos");
		response.getProducts().addAll(products);

		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateProductRequest")
	@ResponsePayload
	public UpdateProductResponse updateProduct(@RequestPayload UpdateProductRequest request) {
		UpdateProductResponse response = new UpdateProductResponse();

		//Obtenemos la instancia del modelo por el id
		ProductDB productDB = productRepository.findById(request.getId()).orElse(null);

		if(productDB == null) {
			response.setStatus("El producto no ha sido encontrado");
			return response;
		}

		//Obtenemos los datos de la respuesta
		String name = request.getName();
		String description = request.getDescription();
		Double price = request.getPrice();
		String category = request.getCategory();

		ValidatorData validator = new ValidatorData();

		//Actualizamos los datos de acuerdo a si se recibieron o no los valores
		if(validator.isNotBlank(name)){
			productDB.setName(name);
		}

		if(validator.isNotBlank(description)){
			productDB.setDescription(description);
		}

		if(validator.isPriceValid(price)){
			productDB.setPrice(price);
		}

		if(validator.isNotBlank(category)){
			//Si la categoria no existe la creamos
			if(!categoryRepository.existsByName(category)){
				//Creamos un objeto category y lo vamos a guardar en la BD
				Category categoryDB = new Category();
				categoryDB.setName(category);
				categoryRepository.save(categoryDB);
			}

			productDB.setCategory(categoryRepository.findByName(category));
		}

		//Guardamos el producto con sus cambios en la base de datos
		productRepository.save(productDB);

		response.setStatus("Producto actualizado correctamente");
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteProductRequest")
	@ResponsePayload
	public DeleteProductResponse deleteProduct(@RequestPayload DeleteProductRequest request){
		DeleteProductResponse response = new DeleteProductResponse();

		//Verificamos que el producto exista
		ProductDB productDB = productRepository.findById(request.getId()).orElse(null);

		if(productDB == null){
			response.setStatus("El producto no existe");
		} else {
			productRepository.delete(productDB);
			response.setStatus("El producto ha sido eliminado exitosamente");
		}

		return response;
	}

}
