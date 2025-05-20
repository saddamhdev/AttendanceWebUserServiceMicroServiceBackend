package com.example.userservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "rolePermission")
@Getter
@Setter
public class Role {
    @Id
    private String id;


    @Indexed(unique = true) // Enforces unique values at the database level
    private String roleName;
    private String roleStatus;
    private List<Menu> menus  = new ArrayList<>();  // Ensure menus is initialized;

    public Role() {
    }

    public Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleStatus() {
        return roleStatus;
    }

    public void setRoleStatus(String roleStatus) {
        this.roleStatus = roleStatus;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }

    @Document
    @Getter
    @Setter
    public static class Menu {
        @Id
        private String id;
        private String menuName;
        private String menuStatus;
        private List<Page> pages  = new ArrayList<>();  // Ensure menus is initialized;

        public Menu() {
        }

        public Menu(String menuName) {
            this.menuName = menuName;
        }

        public String getMenuName() {
            return menuName;
        }

        public void setMenuName(String menuName) {
            this.menuName = menuName;
        }

        public List<Page> getPages() {
            return pages;
        }

        public void setPages(List<Page> pages) {
            this.pages = pages;
        }

        public String getMenuStatus() {
            return menuStatus;
        }

        public void setMenuStatus(String menuStatus) {
            this.menuStatus = menuStatus;
        }

        @Document
        @Getter
        @Setter
        public static class Page {
            @Id
            private String id;
            private String pageName;
            private String pageStatus;
            private List<Component> components  = new ArrayList<>();  // Ensure menus is initialized;

            public Page() {
            }

            public Page(String pageName) {
                this.pageName = pageName;
            }

            public String getPageName() {
                return pageName;
            }

            public void setPageName(String pageName) {
                this.pageName = pageName;
            }

            public List<Component> getComponents() {
                return components;
            }

            public void setComponents(List<Component> components) {
                this.components = components;
            }

            public String getPageStatus() {
                return pageStatus;
            }

            public void setPageStatus(String pageStatus) {
                this.pageStatus = pageStatus;
            }

            @Document
            @Getter
            @Setter
            public static class Component {
                @Id
                private String id;
                private String componentName;
                private  String componentStatus;

                public Component() {
                }

                public Component(String componentName) {
                    this.componentName = componentName;
                }

                public String getComponentName() {
                    return componentName;
                }

                public void setComponentName(String componentName) {
                    this.componentName = componentName;
                }

                public String getComponentStatus() {
                    return componentStatus;
                }

                public void setComponentStatus(String componentStatus) {
                    this.componentStatus = componentStatus;
                }
            }
        }
    }
}
