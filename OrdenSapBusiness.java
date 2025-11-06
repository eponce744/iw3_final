	@Autowired(required = false)
	private OrdenSapRepository ordenDAO;

	@Override
	public OrdenSap load(String codCli1) throws NotFoundException, BusinessException {
		Optional<OrdenSap> r;
		try {
			r = ordenDAO.findOneByCodCli1(codCli1);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (r.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra el Producto codCli1=" + codCli1).build();
		}
		return r.get();
	}

	@Override
	public List<OrdenSap> list() throws BusinessException {
		try {
			return ordenDAO.findAll();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Autowired
	private IProductBusiness productBaseBusiness;

	@Override
	public OrdenSap add(OrdenSap product) throws FoundException, BusinessException {

		try {
			productBaseBusiness.load(product.getId());
			throw FoundException.builder().message("Se encontró el Producto id=" + product.getId()).build();
		} catch (NotFoundException e) {
		}

		if (ordenDAO.findOneByCodCli1(product.getCodCli1()).isPresent()) {
			throw FoundException.builder().message("Se encontró el Producto código=" + product.getCodCli1()).build();
		}


		try {
			return ordenDAO.save(product);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}
	
	@Autowired(required = false)
	private ICategoryBusiness categoryBusiness;

	@Override
	public OrdenSap addExternal(String json) throws FoundException, BusinessException {
		ObjectMapper mapper = JsonUtiles.getObjectMapper(OrdenSap.class,
				new OrdenSapJsonDeserializer(OrdenSap.class, categoryBusiness),null);
		OrdenSap product = null;
		try {
			product = mapper.readValue(json, OrdenSap.class);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}

		return add(product);

	}
