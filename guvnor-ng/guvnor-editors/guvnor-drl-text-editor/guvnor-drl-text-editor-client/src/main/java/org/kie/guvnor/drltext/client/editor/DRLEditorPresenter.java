/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.drltext.client.editor;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.commons.ui.client.save.SaveCommand;
import org.kie.guvnor.commons.ui.client.save.SaveOpWrapper;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.datamodel.service.DataModelService;
import org.kie.guvnor.drltext.client.resources.i18n.DRLTextEditorConstants;
import org.kie.guvnor.drltext.service.DRLTextEditorService;
import org.kie.guvnor.errors.client.widget.ShowBuilderErrorsWidget;
import org.kie.guvnor.metadata.client.resources.i18n.MetaDataConstants;
import org.kie.guvnor.metadata.client.widget.MetadataWidget;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.IsDirty;
import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnMayClose;
import org.uberfire.client.annotations.OnSave;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.common.LoadingPopup;
import org.uberfire.client.common.MultiPageEditor;
import org.uberfire.client.common.Page;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.client.workbench.widgets.menu.MenuBar;

import static org.kie.guvnor.commons.ui.client.menu.ResourceMenuBuilder.*;

/**
 * This is the default rule editor widget (just text editor based).
 */
@Dependent
@WorkbenchEditor(identifier = "DRLEditor", fileTypes = "*.drl")
public class DRLEditorPresenter {

    public interface View
            extends
            IsWidget {

        void setContent( final String content,
                         final DataModelOracle dataModel );

        String getContent();

        boolean isDirty();

        void setNotDirty();

        boolean confirmClose();
    }

    @Inject
    private View view;

    @Inject
    private Caller<DataModelService> dataModelService;

    @Inject
    @New
    private MultiPageEditor multiPage;

    @Inject
    private Caller<DRLTextEditorService> drlTextEditorService;

    @Inject
    private Event<NotificationEvent> notification;


    @Inject
    private Caller<MetadataService> metadataService;

    private final MetadataWidget metadataWidget = new MetadataWidget();

    private Path path;

    @OnStart
    public void onStart( final Path path ) {
        this.path = path;

        multiPage.addWidget(view, DRLTextEditorConstants.INSTANCE.DRL());

        dataModelService.call( new RemoteCallback<DataModelOracle>() {
            @Override
            public void callback( final DataModelOracle model ) {
                drlTextEditorService.call( new RemoteCallback<String>() {
                    @Override
                    public void callback( String response ) {
                        if ( response == null || response.isEmpty() ) {
                            view.setContent( null,
                                             model );
                        } else {
                            view.setContent( response,
                                             model );
                        }
                    }
                } ).load( path );
            }
        } ).getDataModel(path);

        multiPage.addPage(new Page(metadataWidget, MetaDataConstants.INSTANCE.Metadata()) {
            @Override
            public void onFocus() {
                metadataService.call(
                        new RemoteCallback<Metadata>() {
                            @Override
                            public void callback(Metadata metadata) {
                                metadataWidget.setContent(metadata, false);
                            }
                        }
                ).getMetadata(path);
            }

            @Override
            public void onLostFocus() {
                // Nothing to do here
            }
        });
    }

    @OnSave
    public void onSave() {
        new SaveOpWrapper(path, new SaveCommand() {
            @Override
            public void execute(final String commitMessage) {
                drlTextEditorService.call(new RemoteCallback<Path>() {
                    @Override
                    public void callback(Path response) {
                        view.setNotDirty();
                        notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemSavedSuccessfully()));
                    }
                }).save(path,
                        view.getContent(),
                        metadataWidget.getContent(),
                        commitMessage);
            }
        }).save();
    }

    @IsDirty
    public boolean isDirty() {
        return view.isDirty();
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @OnMayClose
    public boolean checkIfDirty() {
        if ( isDirty() ) {
            return view.confirmClose();
        }
        return true;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "DRL Editor [" + path.getFileName() + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

    @WorkbenchMenu
    public MenuBar buildMenuBar() {
        return newResourceMenuBuilder().addValidation( new Command() {
            @Override
            public void execute() {
                LoadingPopup.showMessage( CommonConstants.INSTANCE.WaitWhileValidating() );
                drlTextEditorService.call( new RemoteCallback<BuilderResult>() {
                    @Override
                    public void callback( BuilderResult response ) {
                        final ShowBuilderErrorsWidget pop = new ShowBuilderErrorsWidget( response );
                        LoadingPopup.close();
                        pop.show();
                    }
                } ).validate( path,
                              view.getContent() );
            }
        } ).addSave( new Command() {
            @Override
            public void execute() {
                onSave();
            }
        } ).build();
    }

}