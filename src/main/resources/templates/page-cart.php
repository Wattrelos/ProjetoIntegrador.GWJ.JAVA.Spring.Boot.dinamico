<?php include '../parts/header.html'; ?>

<!-- wp:woocommerce/page-content-wrapper {"page":"cart"} -->
	<!-- wp:group {"tagName":"main","metadata":{"name":"Main"},"align":"full","style":{"spacing":{"padding":{"top":"var:preset|spacing|40","bottom":"var:preset|spacing|50"},"margin":{"top":"0","bottom":"0"}}},"layout":{"type":"constrained"}} -->
	<main class="wp-block-group alignfull" style="margin-top:0;margin-bottom:0;padding-top:var(--wp--preset--spacing--40);padding-bottom:var(--wp--preset--spacing--50)">
		<div class="alignwide">
			<h1 style="margin-bottom: var(--wp--preset--spacing--40);">Seu Carrinho</h1>
			
			<!-- Tabela de Produtos Mockados -->
			<div style="overflow-x: auto;">
				<table style="width: 100%; border-collapse: collapse; margin-bottom: var(--wp--preset--spacing--40); text-align: left;">
					<thead>
						<tr style="border-bottom: 2px solid var(--wp--preset--color--theme-3);">
							<th style="padding: 1rem 0;">Serviço</th>
							<th style="padding: 1rem;">Preço</th>
							<th style="padding: 1rem;">Quantidade</th>
							<th style="padding: 1rem; text-align: right;">Subtotal</th>
						</tr>
					</thead>
					<tbody>
						<tr style="border-bottom: 1px solid color-mix(in srgb, currentColor 20%, transparent);">
							<td style="padding: 1.5rem 0; display: flex; align-items: center; gap: 1rem;">
								<img src="https://images.unsplash.com/photo-1599351431202-1e0f0137899a?w=100&q=80" alt="Corte Clássico" style="width: 60px; height: 60px; object-fit: cover; border-radius: 8px;">
								<strong style="font-size: var(--wp--preset--font-size--medium);">Corte Clássico Masculino</strong>
							</td>
							<td style="padding: 1.5rem 1rem;">R$ 45,00</td>
							<td style="padding: 1.5rem 1rem;">
								<input type="number" value="1" min="1" style="width: 70px; padding: 0.5rem; text-align: center; background: transparent; border: 1px solid currentColor; border-radius: 4px; color: currentColor;">
							</td>
							<td style="padding: 1.5rem 1rem; text-align: right;"><strong>R$ 45,00</strong></td>
						</tr>
						<tr style="border-bottom: 1px solid color-mix(in srgb, currentColor 20%, transparent);">
							<td style="padding: 1.5rem 0; display: flex; align-items: center; gap: 1rem;">
								<img src="https://images.unsplash.com/photo-1621605815971-fbc98d665033?w=100&q=80" alt="Barboterapia" style="width: 60px; height: 60px; object-fit: cover; border-radius: 8px;">
								<strong style="font-size: var(--wp--preset--font-size--medium);">Barboterapia Premium</strong>
							</td>
							<td style="padding: 1.5rem 1rem;">R$ 35,00</td>
							<td style="padding: 1.5rem 1rem;">
								<input type="number" value="1" min="1" style="width: 70px; padding: 0.5rem; text-align: center; background: transparent; border: 1px solid currentColor; border-radius: 4px; color: currentColor;">
							</td>
							<td style="padding: 1.5rem 1rem; text-align: right;"><strong>R$ 35,00</strong></td>
						</tr>
					</tbody>
				</table>
			</div>

			<!-- Resumo da Compra / Checkout -->
			<div style="display: flex; justify-content: flex-end;">
				<div style="background-color: var(--wp--preset--color--theme-2); color: var(--wp--preset--color--theme-5); padding: 2rem; border-radius: 8px; width: 100%; max-width: 400px;">
					<h3 style="margin-top: 0; border-bottom: 1px solid color-mix(in srgb, currentColor 20%, transparent); padding-bottom: 1rem;">Resumo da Reserva</h3>
					<div style="display: flex; justify-content: space-between; padding: 1rem 0; font-size: var(--wp--preset--font-size--medium);">
						<span>Total:</span>
						<strong style="color: var(--wp--preset--color--theme-3);">R$ 80,00</strong>
					</div>
					<a href="checkout.php" class="wp-element-button" style="display: block; text-align: center; background-color: var(--wp--preset--color--theme-5); color: var(--wp--preset--color--theme-1); text-decoration: none; padding: 1rem; border-radius: 4px; margin-top: 1rem;">
						Prosseguir para Pagamento
					</a>
				</div>
			</div>
		</div>
	</main>
	<!-- /wp:group -->
<!-- /wp:woocommerce/page-content-wrapper -->

<?php include '../parts/footer.html'; ?>
