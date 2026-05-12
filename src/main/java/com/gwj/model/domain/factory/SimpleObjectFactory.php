<?php

namespace com\gwj\model\domain\factory;

use com\gwj\AppConfig;
use com\gwj\model\domain\IEntity;
use RuntimeException;
use Exception;

/**
 * Refere-se a SimpleObjectFactory.java
 * Adaptado para PHP 8.4.16
 */
abstract class SimpleObjectFactory
{
    /**
     * Instancia qualquer classe pelo nome, desde que implemente IEntity.
     * 
     * @param string $fullClassName Nome simples da classe (ex: "Cliente")
     * @return IEntity
     * @throws RuntimeException
     */
    public static function create(string $fullClassName): IEntity
    {
        try {
            // 1. Resolve o Namespace completo
            // No PHP, convertemos o ponto (padrão Java no AppConfig) para contra-barra (Namespace PHP)
            $baseNamespace = str_replace('.', '\\', AppConfig::ENTITIES_PATH);
            $targetClass = $baseNamespace . '\\' . $fullClassName;

            // 2. Verifica se a classe existe no sistema de Autoload
            if (!class_exists($targetClass)) {
                throw new RuntimeException("Classe não encontrada: " . $targetClass);
            }

            // 3. Valida se a classe é compatível com IEntity (Equivalente ao isAssignableFrom)
            if (is_subclass_of($targetClass, IEntity::class)) {
                // 4. Cria a instância dinamicamente
                return new $targetClass();
            } else {
                throw new RuntimeException("A classe " . $fullClassName . " não é uma IEntity válida.");
            }
        } catch (Exception $e) {
            throw new RuntimeException("Falha ao instanciar objeto: " . $fullClassName, 0, $e);
        }
    }
}