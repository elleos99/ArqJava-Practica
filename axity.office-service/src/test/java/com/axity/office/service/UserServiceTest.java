package com.axity.office.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.axity.office.commons.dto.RoleDto;
import com.axity.office.commons.dto.UserDto;
import com.axity.office.commons.enums.ErrorCode;
import com.axity.office.commons.exception.BusinessException;
import com.axity.office.commons.request.PaginatedRequestDto;

/**
 * Class UserServiceTest
 * 
 * @author username@axity.com
 */
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Transactional
class UserServiceTest
{
  private static final Logger LOG = LoggerFactory.getLogger( UserServiceTest.class );

  @Autowired
  private UserService userService;

  /**
   * Method to validate the paginated search
   */
  @Test
  void testFindUsers()
  {
    var request = new PaginatedRequestDto();
    request.setLimit( 5 );
    request.setOffset( 0 );
    var users = this.userService.findUsers( request );

    LOG.info( "Response: {}", users );

    assertNotNull( users );
    assertNotNull( users.getData() );
    assertFalse( users.getData().isEmpty() );
  }

  /**
   * Method to validate the search by id
   * 
   * @param userId
   */
  @ParameterizedTest
  @ValueSource(ints = { 1 })
  void testFind( Integer userId )
  {
    var user = this.userService.find( userId );
    assertNotNull( user );
    LOG.info( "Response: {}", user );
  }

  /**
   * Method to validate the search by id inexistent
   */
  @Test
  void testFind_NotExists()
  {
    var user = this.userService.find( 999999 );
    assertNull( user );
  }

  /**
   * Test method for create 
   * {@link com.axity.office.service.impl.UserServiceImpl#create(com.axity.office.commons.dto.UserDto)}.
   */
  @Test
  @Disabled("TODO: Actualizar la prueba de acuerdo a la entidad")
  void testCreate()
  {
    var dto = new UserDto();
    // Crear de acuerdo a la entidad

    var response = this.userService.creat( dto );
    assertNotNull( response );
    assertEquals( 0, response.getHeader().getCode() );
    assertNotNull( response.getBody() );

    this.userService.delete( dto.getId() );
  }

/**
   * Test method for create user whith multiple roles
   * {@link com.axity.office.service.impl.UserServiceImpl#create(com.axity.office.commons.dto.UserDto)}.
   */
  @Test
  void createUser(){
    var list = new ArrayList<RoleDto>();
    list.add(creatRole(1));
    list.add(creatRole(2));
    
    var dto = new UserDto();
    dto.setUsername("LeoUN");
    dto.setEmail("Leo@company.com");
    dto.setName("Leo");
    dto.setLastName("Fuentes");
    dto.setRoles(list); 

    var response = this.userService.creat( dto );
    assertNotNull( response );
    assertEquals( 0, response.getHeader().getCode() );
    assertNotNull( response.getBody() );
  }

  private RoleDto creatRole(int id){
    var role = new RoleDto();
    role.setId(id);
    return role;
  }


  /**
   * Test method for username validation
   * {@link com.axity.office.service.impl.UserServiceImpl#create(com.axity.office.commons.dto.UserDto)}.
   */
  @Test
  void testUsernameExistence() {
    // Data inicial
    var list = new ArrayList<RoleDto>();
    list.add(creatRole(1));

    var dto = new UserDto();

    dto.setUsername("LeoUN");
    dto.setEmail("Leo@company.com");
    dto.setName("Leo");
    dto.setLastName("Fuentes");
    dto.setRoles(list);

    var response = this.userService.creat(dto);
    assertNotNull(response);
    assertNotNull( response.getBody() );
    assertEquals(ErrorCode.USER_ALREADY_EXISTS.getCode(), response.getHeader().getCode());
  }

  /**
   * Test method for validate email already exist in bd
   * {@link com.axity.office.service.impl.UserServiceImpl#create(com.axity.office.commons.dto.UserDto)}.
   */
  @Test
  void testMailExistence() {
    // Data inicial
    var list = new ArrayList<RoleDto>();
    list.add(creatRole(1));

    var dto = new UserDto();
    
    dto.setUsername("LeoUN");
    dto.setEmail("denise.alford@company.com");
    dto.setName("Leo");
    dto.setLastName("Fuentes");
    dto.setRoles(list);

    var response = this.userService.creat(dto);
    assertNotNull(response);
    assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS.getCode(), response.getHeader().getCode());
  }

  /**
   * Test method for role validation
   * {@link com.axity.office.service.impl.UserServiceImpl#create(com.axity.office.commons.dto.UserDto)}.
   */
  @Test
  void testRoles() {
    // Data inicial
    var list = new ArrayList<RoleDto>();
    list.add(creatRole(99)); 

    var dto = new UserDto();

    dto.setUsername("LeoUN");
    dto.setEmail("Leo@company.com");
    dto.setName("Leo");
    dto.setLastName("Fuentes");
    dto.setRoles(list);

    var response = this.userService.creat(dto);
    assertNotNull(response);
    assertEquals(ErrorCode.ROLE_NOT_EXISTS.getCode(), response.getHeader().getCode());
  }

  /**
   * Test method for roles empty validation
   * {@link com.axity.office.service.impl.UserServiceImpl#create(com.axity.office.commons.dto.UserDto)}.
   */
  @Test
  void testEmptyRoles() {
    // Data inicial
    var list = new ArrayList<RoleDto>();// Roles list empty does not exist in db

    var dto = new UserDto();
    dto.setUsername("LeoUN");
    dto.setEmail("Leo@company.com");
    dto.setName("Leo");
    dto.setLastName("Fuentes");
    dto.setRoles(list);

    // Llamada
    var response = this.userService.creat(dto);

    // Validación
    assertNotNull(response);
    assertEquals(ErrorCode.ROLE_NOT_SELECTED.getCode(), response.getHeader().getCode());
  }

  /**
   * Method to validate update
   */
  @Test
  @Disabled("TODO: Actualizar la prueba de acuerdo a la entidad")
  void testUpdate()
  {
    var user = this.userService.find( 1 ).getBody();
    // TODO: actualizar de acuerdo a la entidad

    var response = this.userService.update( user );

    assertNotNull( response );
    assertEquals( 0, response.getHeader().getCode() );
    assertTrue( response.getBody() );
    user = this.userService.find( 1 ).getBody();

    // Verificar que se actualice el valor
  }

  /**
   * Method to validate an inexistent registry
   */
  @Test
  void testUpdate_NotFound()
  {
    var user = new UserDto();
    user.setId(999999);
    var ex = assertThrows( BusinessException.class, () -> this.userService.update( user ) );

    assertEquals( ErrorCode.OFFICE_NOT_FOUND.getCode(), ex.getCode() );
  }

  /**
   * Test method for {@link com.axity.office.service.impl.UserServiceImpl#delete(java.lang.String)}.
   */
  @Test
  void testDeleteNotFound()
  {
    var ex = assertThrows( BusinessException.class, () -> this.userService.delete( 999999 ) );
    assertEquals( ErrorCode.OFFICE_NOT_FOUND.getCode(), ex.getCode() );
  }
}
