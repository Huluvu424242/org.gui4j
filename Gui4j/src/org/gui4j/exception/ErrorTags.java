package org.gui4j.exception;

/**
 * This interface contains only error tag declarations
 */
public interface ErrorTags
{
    String EXCEPTION_OCCURED = "exception_occured";
    String RESOURCE_ERROR = "resource_error";
    String PROGRAMMING_ERROR = "programming_error";

    /**
     * Parameters: stringValue
    */
    String RESOURCE_ERROR_int_DataConversionException = "resource_error_int_DataConversionException";
    
    /**
     * Parameters: stringValue
    */
    String RESOURCE_ERROR_double_DataConversionException = "resource_error_double_DataConversionException";
    
    /**
     * Parameters: currentType, expectedType, accessPath
    */
    String RESOURCE_ERROR_access_type_not_compatible = "resource_error_access_type_not_compatible";
    
    /**
     * Parameters: currentType, expectedType
    */
    String RESOURCE_ERROR_type_not_compatible = "resource_error_type_not_compatible";
    
    /**
     * Exception
    */
    String RESOURCE_ERROR_jdom_exception = "resource_error_jdom_exception";
    
    /**
     * Parameter: relUrl
    */
    String RESOURCE_ERROR_invalid_url = "resource_error_invalid_url";
    
    /**
     * Parameter: id, includeURL
    */
    String RESOURCE_ERROR_unknown_param = "resource_error_unknown_param";
    
    /**
     * Parameter: id, includeURL
    */
    String RESOURCE_ERROR_unknown_param_in_include = "resource_error_unknown_param_in_include";
    
    /**
     * Parameter: aliasName, className
    */
    String RESOURCE_ERROR_alias_class_not_found = "resource_error_alias_class_not_found";
    
    /**
     * Parameter: accessPath, aliasName
    */
    String RESOURCE_ERROR_alias_not_defined_in_path = "resource_error_alias_not_defined_in_path";
    
    /**
     * Parameter: aliasName
    */
    String RESOURCE_ERROR_alias_undefined = "resource_error_alias_undefined";
    
    /**
     * Parameter: aliasName
    */
    String RESOURCE_ERROR_alias_already_defined = "resource_error_alias_already_defined";
    
    /**
     * Parameter: accessPath, position
    */
    String RESOURCE_ERROR_access_unexpected_character = "resource_error_access_unexpected_character";
    
    /**
     * Parameter: accessPath, position
    */
    String RESOURCE_ERROR_access_unexpected_end = "resource_error_access_unexpected_end";
    
    /**
     * Parameter: accessPath
    */
    String RESOURCE_ERROR_access_value_type_not_defined = "resource_error_access_value_type_not_defined";
    
    /**
     * Parameters: placementTag
    */
    String RESOURCE_ERROR_element_must_contain_gui4jComponent = "resource_error_element_must_contain_gui4jComponent";
    
    /**
     * Parameters: id
    */
    String RESOURCE_ERROR_gui4jComponent_already_defined = "resource_error_gui4jComponent_already_defined";
    
    /**
     * Parameters: id
    */
    String RESOURCE_ERROR_gui4jComponent_not_defined = "resource_error_gui4jComponent_not_defined";
    
    /**
     * Parameters: name
    */
    String RESOURCE_ERROR_gui4jComponent_not_registered = "resource_error_gui4jComponent_not_registered";
    
    /**
     * Parameters: name
    */
    String RESOURCE_ERROR_gui4jComponent_already_registered = "resource_error_gui4jComponent_already_registered";
    
    /**
     * Parameters: row, col
    */
    String RESOURCE_ERROR_element_at_row_col_already_defined = "resource_error_element_at_row_col_already_defined";
    
    /**
     * Parameters: row, rows
    */
    String RESOURCE_ERROR_invalid_row = "resource_invalid_row";
    
    /**
     * Parameters: column, columns
    */
    String RESOURCE_ERROR_invalid_column = "resource_invalid_column";
    
    /**
     * Parameter: styleName
    */
    String RESOURCE_ERROR_style_defined_twice = "resource_error_style_defined_twice";
    
    /**
     * Parameter: styleName
    */
    String RESOURCE_ERROR_style_not_defined = "resource_error_style_not_defined";
    
    /**
     * Parameter: propertyName, methodResultType, gui4jAccessArgumentType
    */
    String RESOURCE_ERROR_property_getter_type_incompatible = "resource_error_property_getter_type_incompatible";

    /**
     * Parameters: tag, attribute
    */
    String RESOURCE_ERROR_attribute_not_defined = "resource_error_attribute_not_defined";
    
    /**
     * No args
    */
    String RESOURCE_ERROR_attribute_listEditable_defined = "resource_error_attribute_listEditable_defined";

    /**
     * No args
    */
    String RESOURCE_ERROR_attribute_editable_defined = "resource_error_attribute_editable_defined";
    
    /**
     * Parameters: id
     */
    String RESOURCE_ERROR_invalid_defaultButton = "resource_error_invalid_defaultButton";
    
    /**
     * Parameters: keystroke string
     */
    String RESOURCE_ERROR_invalid_keystroke = "resource_error_invalid_keystroke";
    
    /**
     * Parameters: str
     */
    String RESOURCE_ERROR_tableLayout_invalid_col_row_str = "resource_error_tableLayout_invalid_col_row_str";
    
    /**
     * No args
     */
    String RESOURCE_ERROR_unexpected_gui4jStyle_end = "resource_error_unexpected_gui4jStyle_end";
    
    /**
     * No args
     */
    String RESOURCE_ERROR_labelform_column_conflict = "resource_error_labelform_column_conflict";
    
    /**
     * No args
    */
    String PROGRAMMING_ERROR_parameter_null = "programming_error_parameter_null";
    
    /**
     * No args
    */
    String PROGRAMMING_ERROR_invocation_target_exception = "programming_error_invocation_target_exception";
    
    /**
     * No args
    */
    String PROGRAMMING_ERROR_illegal_access_exception = "programming_error_illegal_access_exception";
    
    /**
     * No args
    */
    String PROGRAMMING_ERROR_instantiation_exception = "programming_error_instantiation_exception";
    
    /**
     * Parameters: class, methodName, signature
    */
    String PROGRAMMING_ERROR_method_ambiguous = "programming_error_method_ambiguous";
    
    /**
     * Parameters: class, methodName, signature, context
    */
    String PROGRAMMING_ERROR_method_not_found = "programming_error_method_not_found";
    
}
