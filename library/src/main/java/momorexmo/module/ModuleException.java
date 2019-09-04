package momorexmo.module;

public class ModuleException extends NullPointerException
{


    public ModuleException(String exceptionMessage)
    {
        super(exceptionMessage);
        throw this;
    }
    public ModuleException(int exceptionMessageID) {
        super(AppRichActivity.getActivity().getResources().getString(exceptionMessageID));
        throw this;
    }


}
