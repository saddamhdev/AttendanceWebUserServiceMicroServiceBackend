package com.example.userservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Document(collection = "Developer")
public class Developer {
    @Id
    private String id;
    private String menuName;
    private String menuStatus;
    private List<Page> pages;

    public Developer() {
    }

    public Developer(String menuName) {
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

    @Setter
    @Getter
    public static class Page {
        @Id
        private String id;
        private String pageName;
        private String pageStatus;
        private List<Component> components;

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

        @Setter
        @Getter
        public static class Component {
            @Id
            private String id;
            private String componentName;
            private String componentStatus;
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
