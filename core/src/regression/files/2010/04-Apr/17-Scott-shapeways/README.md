

This file is blowing up the heap, as you can see below, but only on the current branch.

The only Java changes on this branch are to refactor Tool Factory classes to their own files.
See commit 9e09b72b4982117c4fad92df131676e04e3ac70a

```
java.lang.OutOfMemoryError: Java heap space
        at java.base/java.util.Arrays.copyOf(Arrays.java:3481)
        at java.base/java.util.ArrayList.grow(ArrayList.java:237)
        at java.base/java.util.ArrayList.grow(ArrayList.java:244)
        at java.base/java.util.ArrayList.add(ArrayList.java:454)
        at java.base/java.util.ArrayList.add(ArrayList.java:467)
        at com.vzome.core.editor.api.SideEffects.plan(SideEffects.java:82)
        at com.vzome.core.editor.api.ChangeSelection.selectGroup(ChangeSelection.java:207)
        at com.vzome.core.editor.api.ChangeSelection.selectGroup(ChangeSelection.java:205)
        at com.vzome.core.editor.api.ChangeSelection.select(ChangeSelection.java:198)
        at com.vzome.core.editor.api.ChangeSelection.select(ChangeSelection.java:169)
        at com.vzome.core.edits.ShowHidden.perform(ShowHidden.java:24)
        at com.vzome.core.editor.EditHistory$DeferredEdit$1.performAndRecord(EditHistory.java:629)
        at com.vzome.core.editor.api.ChangeSelection.loadAndPerform(ChangeSelection.java:114)
        at com.vzome.core.editor.EditHistory$DeferredEdit.redo(EditHistory.java:622)
        at com.vzome.core.editor.EditHistory.synchronize(EditHistory.java:752)
        at com.vzome.core.editor.DocumentModel.finishLoading(DocumentModel.java:660)
        at com.vzome.api.Application.loadDocument(Application.java:77)
        at com.vzome.api.Application.loadDocument(Application.java:70)
        at com.vzome.core.regression.TestVZomeFiles.actOnFile(TestVZomeFiles.java:212)
        at com.vzome.core.regression.FileSystemVisitor2.visitFileOrFolder(FileSystemVisitor2.java:132)
        at com.vzome.core.regression.FileSystemVisitor2$Actor.actOnFileOrFolder(FileSystemVisitor2.java:48)
        at com.vzome.core.regression.FileSystemVisitor2.visitFolder(FileSystemVisitor2.java:141)
        at com.vzome.core.regression.TestVZomeFiles$Collector.visitFolder(TestVZomeFiles.java:166)
        at com.vzome.core.regression.FileSystemVisitor2$Actor.actOnFolder(FileSystemVisitor2.java:123)
        at com.vzome.core.regression.TestVZomeFiles.actOnFolder(TestVZomeFiles.java:175)
        at com.vzome.core.regression.FileSystemVisitor2.visitFileOrFolder(FileSystemVisitor2.java:130)
        at com.vzome.core.regression.FileSystemVisitor2$Actor.actOnFileOrFolder(FileSystemVisitor2.java:48)
        at com.vzome.core.regression.FileSystemVisitor2.visitFolder(FileSystemVisitor2.java:141)
        at com.vzome.core.regression.TestVZomeFiles$Collector.visitFolder(TestVZomeFiles.java:166)
        at com.vzome.core.regression.FileSystemVisitor2$Actor.actOnFolder(FileSystemVisitor2.java:123)
        at com.vzome.core.regression.TestVZomeFiles.actOnFolder(TestVZomeFiles.java:175)
        at com.vzome.core.regression.FileSystemVisitor2.visitFileOrFolder(FileSystemVisitor2.java:130)
```
