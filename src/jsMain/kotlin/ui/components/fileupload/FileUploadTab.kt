package ui.components.fileupload

import css.component.page.TabBarStyle
import kotlinx.css.Align
import kotlinx.css.Display
import kotlinx.css.alignItems
import kotlinx.css.display
import model.FileInfo
import model.IssueSeverity
import model.ValidationMessage
import model.ValidationOutcome
import react.*
import styled.css
import styled.styledDiv
import ui.components.fileupload.filelist.fileEntry

external interface FileUploadTabProps : RProps {
    var active: Boolean
}

/**
 * Component displaying the list of uploaded files, in addition to the controls for uploading and starting validation.
 */
class FileUploadTab : RComponent<FileUploadTabProps, RState>() {

    override fun RBuilder.render() {
        styledDiv {
            css {
                // If the tab is currently displayed on screen, we define a layout type, otherwise, we set to none
                display = if (props.active) Display.flex else Display.none
                alignItems = Align.flexStart
                +TabBarStyle.body
            }
            fileEntry {
                validationOutcome = ValidationOutcome()
                    .setFileInfo(FileInfo().setFileName("test_file.json"))
                    .setValidated(true)
                    .setValidating(false)
                    .setMessages(listOf(ValidationMessage().setLevel(level = IssueSeverity.FATAL)))
                onDelete = {
                    println("Upper level delete called")
                }
            }
//            fileUploadComponent {}
//            fileUploadButtonBar { }
//            fileItemOptions {
//                viewOption = true
//            }
//            styledDiv {
//                css {
//                    display = Display.flex
//                    flexDirection = FlexDirection.row
//                }
//                fileStatusIndicator {
//                    validationOutcome = ValidationOutcome()
//                        .setValidated(false)
//                        .setValidating(false)
//                }
//                fileStatusIndicator {
//                    validationOutcome = ValidationOutcome()
//                        .setValidated(true)
//                        .setValidating(false)
//                        .setMessages(listOf(ValidationMessage().setLevel(level = IssueSeverity.INFORMATION)))
//                }
//                fileStatusIndicator {
//                    validationOutcome = ValidationOutcome()
//                        .setValidated(false)
//                        .setValidating(true)
//                }
//                fileStatusIndicator {
//                    validationOutcome = ValidationOutcome()
//                        .setValidated(true)
//                        .setValidating(false)
//                        .setMessages(listOf(ValidationMessage().setLevel(level = IssueSeverity.WARNING)))
//                }
//                fileStatusIndicator {
//                    validationOutcome = ValidationOutcome()
//                        .setValidated(true)
//                        .setValidating(false)
//                        .setMessages(listOf(ValidationMessage().setLevel(level = IssueSeverity.ERROR)))
//                }
//                fileStatusIndicator {
//                    validationOutcome = ValidationOutcome()
//                        .setValidated(true)
//                        .setValidating(false)
//                        .setMessages(listOf(ValidationMessage().setLevel(level = IssueSeverity.FATAL)))
//                }
//            }
        }
    }
}

fun RBuilder.fileUploadTab(handler: FileUploadTabProps.() -> Unit): ReactElement {
    return child(FileUploadTab::class) {
        this.attrs(handler)
    }
}